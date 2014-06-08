package com.openfarmanager.android.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * author: Vlad Namashko
 */
public class FontManager {

    /**
     * Enumerates all fonts on Android system and returns the HashMap with the font
     * absolute file name as key, and the font literal name (embedded into the font) as value.
     *
     * @return fontPath/fontName pairs.
     */
    static public HashMap<String, String> enumerateFonts() {
        String[] fontDirs = {"/system/fonts", "/system/font", "/data/fonts"};
        HashMap<String, String> fonts = new HashMap<String, String>();
        TTFAnalyzer analyzer = new TTFAnalyzer();

        for (String fontDir : fontDirs) {
            File dir = new File(fontDir);

            if (!dir.exists())
                continue;

            File[] files = dir.listFiles();

            if (files == null)
                continue;

            for (File file : files) {
                String fontName = analyzer.getTtfFontName(file.getAbsolutePath());

                if (fontName != null)
                    fonts.put(file.getAbsolutePath(), fontName);
            }
        }

        return fonts.isEmpty() ? null : fonts;
    }
}

class TTFAnalyzer {
    // This function parses the TTF file and returns the font name specified in the file

    /**
     * Parse TTF file and return the font name specified in the file.
     *
     * @param fontFilename path to font file
     * @return font name
     */
    public String getTtfFontName(String fontFilename) {
        try {

            // Parses the TTF file format.
            // See http://developer.apple.com/fonts/ttrefman/rm06/Chap6.html
            mFile = new RandomAccessFile(fontFilename, "r");

            // Read the version first
            int version = readDword();

            // The version must be either 'true' (0x74727565) or 0x00010000
            if (version != 0x74727565 && version != 0x00010000)
                return null;

            // The TTF file consist of several sections called "tables", and we need to know how many of them are there.
            int numTables = readWord();

            // Skip the rest in the header
            readWord(); // skip searchRange
            readWord(); // skip entrySelector
            readWord(); // skip rangeShift

            // Now we can read the tables
            for (int i = 0; i < numTables; i++) {
                // Read the table entry
                int tag = readDword();
                readDword(); // skip checksum
                int offset = readDword();
                int length = readDword();

                // Now here' the trick. 'name' field actually contains the textual string name.
                // So the 'name' string in characters equals to 0x6E616D65
                if (tag == 0x6E616D65) {
                    // Here's the name section. Read it completely into the allocated buffer
                    byte[] table = new byte[length];

                    mFile.seek(offset);
                    read(table);

                    // This is also a table. See http://developer.apple.com/fonts/ttrefman/rm06/Chap6name.html
                    // According to Table 36, the total number of table records is stored in the second word, at the offset 2.
                    // Getting the count and string offset - remembering it's big endian.
                    int count = getWord(table, 2);
                    int stringOffset = getWord(table, 4);

                    // Record starts from offset 6
                    for (int record = 0; record < count; record++) {
                        // Table 37 tells us that each record is 6 words -> 12 bytes, and that the nameID is 4th word so its offset is 6.
                        // We also need to account for the first 6 bytes of the header above (Table 36), so...
                        int nameIdOffset = record * 12 + 6;
                        int platformID = getWord(table, nameIdOffset);
                        int nameIdValue = getWord(table, nameIdOffset + 6);

                        // Table 42 lists the valid name Identifiers. We're interested in 4 but not in Unicode encoding (for simplicity).
                        // The encoding is stored as PlatformID and we're interested in Mac encoding
                        if (nameIdValue == 4 && platformID == 1) {
                            // We need the string offset and length, which are the word 6 and 5 respectively
                            int nameLength = getWord(table, nameIdOffset + 8);
                            int nameOffset = getWord(table, nameIdOffset + 10);

                            // The real name string offset is calculated by adding the stringOffset
                            nameOffset = nameOffset + stringOffset;

                            // Make sure it is inside the array
                            if (nameOffset >= 0 && nameOffset + nameLength < table.length)
                                return new String(table, nameOffset, nameLength);
                        }
                    }
                }
            }

            return null;
        } catch (FileNotFoundException e) {
            // Permissions?
            return null;
        } catch (IOException e) {
            // Most likely a corrupted font file
            return null;
        }
    }

    /**
     * Font file; must be seekable
     */
    private RandomAccessFile mFile = null;

    // Helper I/O functions

    private int readByte() throws IOException {
        return mFile.read() & 0xFF;
    }

    private int readWord() throws IOException {
        int b1 = readByte();
        int b2 = readByte();

        return b1 << 8 | b2;
    }

    private int readDword() throws IOException {
        int b1 = readByte();
        int b2 = readByte();
        int b3 = readByte();
        int b4 = readByte();

        return b1 << 24 | b2 << 16 | b3 << 8 | b4;
    }

    private void read(byte[] array) throws IOException {
        if (mFile.read(array) != array.length)
            throw new IOException();
    }

    // Helper
    private int getWord(byte[] array, int offset) {
        int b1 = array[offset] & 0xFF;
        int b2 = array[offset + 1] & 0xFF;

        return b1 << 8 | b2;
    }
}
