package com.openfarmanager.android.core.archive

import java.util.HashMap

object MimeTypes {

    const val MIME_APPLICATION_ANDREW_INSET = "application/andrew-inset"
    const val MIME_APPLICATION_JSON = "application/json"
    const val MIME_APPLICATION_ZIP = "application/zip"
    const val MIME_APPLICATION_7Z = "application/x-7z-compressed"
    const val MIME_APPLICATION_ARJ = "application/arj"
    const val MIME_APPLICATION_X_GZIP = "application/x-gzip"
    const val MIME_APPLICATION_TGZ = "application/tgz"
    const val MIME_APPLICATION_MSWORD = "application/msword"
    const val MIME_APPLICATION_POSTSCRIPT = "application/postscript"
    const val MIME_APPLICATION_PDF = "application/pdf"
    const val MIME_APPLICATION_JNLP = "application/jnlp"
    const val MIME_APPLICATION_MAC_BINHEX40 = "application/mac-binhex40"
    const val MIME_APPLICATION_MAC_COMPACTPRO = "application/mac-compactpro"
    const val MIME_APPLICATION_MATHML_XML = "application/mathml+xml"
    const val MIME_APPLICATION_OCTET_STREAM = "application/octet-stream"
    const val MIME_APPLICATION_ODA = "application/oda"
    const val MIME_APPLICATION_RDF_XML = "application/rdf+xml"
    const val MIME_APPLICATION_JAVA_ARCHIVE = "application/java-archive"
    const val MIME_APPLICATION_RDF_SMIL = "application/smil"
    const val MIME_APPLICATION_SRGS = "application/srgs"
    const val MIME_APPLICATION_SRGS_XML = "application/srgs+xml"
    const val MIME_APPLICATION_VND_MIF = "application/vnd.mif"
    const val MIME_APPLICATION_VND_MSEXCEL = "application/vnd.ms-excel"
    const val MIME_APPLICATION_VND_MSPOWERPOINT = "application/vnd.ms-powerpoint"
    const val MIME_APPLICATION_VND_RNREALMEDIA = "application/vnd.rn-realmedia"
    const val MIME_APPLICATION_X_BCPIO = "application/x-bcpio"
    const val MIME_APPLICATION_X_CDLINK = "application/x-cdlink"
    const val MIME_APPLICATION_X_CHESS_PGN = "application/x-chess-pgn"
    const val MIME_APPLICATION_X_CPIO = "application/x-cpio"
    const val MIME_APPLICATION_X_CSH = "application/x-csh"
    const val MIME_APPLICATION_X_DIRECTOR = "application/x-director"
    const val MIME_APPLICATION_X_DVI = "application/x-dvi"
    const val MIME_APPLICATION_X_FUTURESPLASH = "application/x-futuresplash"
    const val MIME_APPLICATION_X_GTAR = "application/x-gtar"
    const val MIME_APPLICATION_X_HDF = "application/x-hdf"
    const val MIME_APPLICATION_X_JAVASCRIPT = "application/x-javascript"
    const val MIME_APPLICATION_X_KOAN = "application/x-koan"
    const val MIME_APPLICATION_X_LATEX = "application/x-latex"
    const val MIME_APPLICATION_X_NETCDF = "application/x-netcdf"
    const val MIME_APPLICATION_X_OGG = "application/x-ogg"
    const val MIME_APPLICATION_X_SH = "rapplication/x-sh"
    const val MIME_APPLICATION_X_SHAR = "application/x-shar"
    const val MIME_APPLICATION_X_SHOCKWAVE_FLASH = "application/x-shockwave-flash"
    const val MIME_APPLICATION_X_STUFFIT = "application/x-stuffit"
    const val MIME_APPLICATION_X_SV4CPIO = "application/x-sv4cpio"
    const val MIME_APPLICATION_X_SV4CRC = "application/x-sv4crc"
    const val MIME_APPLICATION_X_AR = "application/x-ar"
    const val MIME_APPLICATION_X_TAR = "application/x-tar"
    const val MIME_APPLICATION_X_PACK200 = "application/x-pack200"
    const val MIME_APPLICATION_X_BZIP2 = "application/x-bzip2"
    const val MIME_APPLICATION_X_XZ = "application/x-xz"
    const val MIME_APPLICATION_X_RAR_COMPRESSED = "application/x-rar-compressed"
    const val MIME_APPLICATION_X_TCL = "application/x-tcl"
    const val MIME_APPLICATION_X_TEX = "application/x-tex"
    const val MIME_APPLICATION_X_TEXINFO = "application/x-texinfo"
    const val MIME_APPLICATION_X_TROFF = "application/x-troff"
    const val MIME_APPLICATION_X_TROFF_MAN = "application/x-troff-man"
    const val MIME_APPLICATION_X_TROFF_ME = "application/x-troff-me"
    const val MIME_APPLICATION_X_TROFF_MS = "application/x-troff-ms"
    const val MIME_APPLICATION_X_USTAR = "application/x-ustar"
    const val MIME_APPLICATION_X_WAIS_SOURCE = "application/x-wais-source"
    const val MIME_APPLICATION_VND_MOZZILLA_XUL_XML = "application/vnd.mozilla.xul+xml"
    const val MIME_APPLICATION_XHTML_XML = "application/xhtml+xml"
    const val MIME_APPLICATION_XSLT_XML = "application/xslt+xml"
    const val MIME_APPLICATION_XML = "application/xml"
    const val MIME_APPLICATION_ANDROID_PACKAGE = "application/xml-dtd"
    const val MIME_APPLICATION_XML_DTD = "application/vnd.android.package-archive"
    const val MIME_IMAGE = "image/*"
    const val MIME_IMAGE_BMP = "image/bmp"
    const val MIME_IMAGE_CGM = "image/cgm"
    const val MIME_IMAGE_GIF = "image/gif"
    const val MIME_IMAGE_IEF = "image/ief"
    const val MIME_IMAGE_JPEG = "image/jpeg"
    const val MIME_IMAGE_TIFF = "image/tiff"
    const val MIME_IMAGE_PNG = "image/png"
    const val MIME_IMAGE_SVG_XML = "image/svg+xml"
    const val MIME_IMAGE_VND_DJVU = "image/vnd.djvu"
    const val MIME_IMAGE_WAP_WBMP = "image/vnd.wap.wbmp"
    const val MIME_IMAGE_X_CMU_RASTER = "image/x-cmu-raster"
    const val MIME_IMAGE_X_ICON = "image/x-icon"
    const val MIME_IMAGE_X_PORTABLE_ANYMAP = "image/x-portable-anymap"
    const val MIME_IMAGE_X_PORTABLE_BITMAP = "image/x-portable-bitmap"
    const val MIME_IMAGE_X_PORTABLE_GRAYMAP = "image/x-portable-graymap"
    const val MIME_IMAGE_X_PORTABLE_PIXMAP = "image/x-portable-pixmap"
    const val MIME_IMAGE_X_RGB = "image/x-rgb"
    const val MIME_AUDIO = "audio/*"
    const val MIME_AUDIO_BASIC = "audio/basic"
    const val MIME_AUDIO_MIDI = "audio/midi"
    const val MIME_AUDIO_MPEG = "audio/mpeg"
    const val MIME_AUDIO_X_AIFF = "audio/x-aiff"
    const val MIME_AUDIO_X_MPEGURL = "audio/x-mpegurl"
    const val MIME_AUDIO_X_PN_REALAUDIO = "audio/x-pn-realaudio"
    const val MIME_AUDIO_X_WAV = "audio/x-wav"
    const val MIME_CHEMICAL_X_PDB = "chemical/x-pdb"
    const val MIME_CHEMICAL_X_XYZ = "chemical/x-xyz"
    const val MIME_MODEL_IGES = "model/iges"
    const val MIME_MODEL_MESH = "model/mesh"
    const val MIME_MODEL_VRLM = "model/vrml"
    const val MIME_TEXT_PLAIN = "text/plain"
    const val MIME_TEXT_RICHTEXT = "text/richtext"
    const val MIME_TEXT_RTF = "text/rtf"
    const val MIME_TEXT_HTML = "text/html"
    const val MIME_TEXT_CALENDAR = "text/calendar"
    const val MIME_TEXT_CSS = "text/css"
    const val MIME_TEXT_SGML = "text/sgml"
    const val MIME_TEXT_TAB_SEPARATED_VALUES = "text/tab-separated-values"
    const val MIME_TEXT_VND_WAP_XML = "text/vnd.wap.wml"
    const val MIME_TEXT_VND_WAP_WMLSCRIPT = "text/vnd.wap.wmlscript"
    const val MIME_TEXT_X_SETEXT = "text/x-setext"
    const val MIME_TEXT_X_COMPONENT = "text/x-component"
    const val MIME_VIDEO = "video/*"
    const val MIME_VIDEO_QUICKTIME = "video/quicktime"
    const val MIME_VIDEO_MPEG = "video/mpeg"
    const val MIME_VIDEO_VND_MPEGURL = "video/vnd.mpegurl"
    const val MIME_VIDEO_X_MSVIDEO = "video/x-msvideo"
    const val MIME_VIDEO_X_MS_WMV = "video/x-ms-wmv"
    const val MIME_VIDEO_X_SGI_MOVIE = "video/x-sgi-movie"
    const val MIME_X_CONFERENCE_X_COOLTALK = "x-conference/x-cooltalk"

    private var mimeTypeMapping: HashMap<String, String> = HashMap()

    init {

        putType("xul", MIME_APPLICATION_VND_MOZZILLA_XUL_XML)
        putType("json", MIME_APPLICATION_JSON)
        putType("ice", MIME_X_CONFERENCE_X_COOLTALK)
        putType("movie", MIME_VIDEO_X_SGI_MOVIE)
        putType("avi", MIME_VIDEO_X_MSVIDEO)
        putType("wmv", MIME_VIDEO_X_MS_WMV)
        putType("m4u", MIME_VIDEO_VND_MPEGURL)
        putType("mxu", MIME_VIDEO_VND_MPEGURL)
        putType("htc", MIME_TEXT_X_COMPONENT)
        putType("etx", MIME_TEXT_X_SETEXT)
        putType("wmls", MIME_TEXT_VND_WAP_WMLSCRIPT)
        putType("wml", MIME_TEXT_VND_WAP_XML)
        putType("tsv", MIME_TEXT_TAB_SEPARATED_VALUES)
        putType("sgm", MIME_TEXT_SGML)
        putType("sgml", MIME_TEXT_SGML)
        putType("css", MIME_TEXT_CSS)
        putType("ifb", MIME_TEXT_CALENDAR)
        putType("ics", MIME_TEXT_CALENDAR)
        putType("wrl", MIME_MODEL_VRLM)
        putType("vrlm", MIME_MODEL_VRLM)
        putType("silo", MIME_MODEL_MESH)
        putType("mesh", MIME_MODEL_MESH)
        putType("msh", MIME_MODEL_MESH)
        putType("iges", MIME_MODEL_IGES)
        putType("igs", MIME_MODEL_IGES)
        putType("rgb", MIME_IMAGE_X_RGB)
        putType("ppm", MIME_IMAGE_X_PORTABLE_PIXMAP)
        putType("pgm", MIME_IMAGE_X_PORTABLE_GRAYMAP)
        putType("pbm", MIME_IMAGE_X_PORTABLE_BITMAP)
        putType("pnm", MIME_IMAGE_X_PORTABLE_ANYMAP)
        putType("ico", MIME_IMAGE_X_ICON)
        putType("ras", MIME_IMAGE_X_CMU_RASTER)
        putType("wbmp", MIME_IMAGE_WAP_WBMP)
        putType("djv", MIME_IMAGE_VND_DJVU)
        putType("djvu", MIME_IMAGE_VND_DJVU)
        putType("svg", MIME_IMAGE_SVG_XML)
        putType("ief", MIME_IMAGE_IEF)
        putType("cgm", MIME_IMAGE_CGM)
        putType("bmp", MIME_IMAGE_BMP)
        putType("xyz", MIME_CHEMICAL_X_XYZ)
        putType("pdb", MIME_CHEMICAL_X_PDB)
        putType("ra", MIME_AUDIO_X_PN_REALAUDIO)
        putType("ram", MIME_AUDIO_X_PN_REALAUDIO)
        putType("m3u", MIME_AUDIO_X_MPEGURL)
        putType("aifc", MIME_AUDIO_X_AIFF)
        putType("aif", MIME_AUDIO_X_AIFF)
        putType("aiff", MIME_AUDIO_X_AIFF)
        putType("mp3", MIME_AUDIO_MPEG)
        putType("mp2", MIME_AUDIO_MPEG)
        putType("mp1", MIME_AUDIO_MPEG)
        putType("mpga", MIME_AUDIO_MPEG)
        putType("kar", MIME_AUDIO_MIDI)
        putType("mid", MIME_AUDIO_MIDI)
        putType("midi", MIME_AUDIO_MIDI)
        putType("dtd", MIME_APPLICATION_XML_DTD)
        putType("xsl", MIME_APPLICATION_XML)
        putType("xml", MIME_APPLICATION_XML)
        putType("xslt", MIME_APPLICATION_XSLT_XML)
        putType("xht", MIME_APPLICATION_XHTML_XML)
        putType("xhtml", MIME_APPLICATION_XHTML_XML)
        putType("src", MIME_APPLICATION_X_WAIS_SOURCE)
        putType("ustar", MIME_APPLICATION_X_USTAR)
        putType("ms", MIME_APPLICATION_X_TROFF_MS)
        putType("me", MIME_APPLICATION_X_TROFF_ME)
        putType("man", MIME_APPLICATION_X_TROFF_MAN)
        putType("roff", MIME_APPLICATION_X_TROFF)
        putType("tr", MIME_APPLICATION_X_TROFF)
        putType("t", MIME_APPLICATION_X_TROFF)
        putType("texi", MIME_APPLICATION_X_TEXINFO)
        putType("texinfo", MIME_APPLICATION_X_TEXINFO)
        putType("tex", MIME_APPLICATION_X_TEX)
        putType("tcl", MIME_APPLICATION_X_TCL)
        putType("sv4crc", MIME_APPLICATION_X_SV4CRC)
        putType("sv4cpio", MIME_APPLICATION_X_SV4CPIO)
        putType("sit", MIME_APPLICATION_X_STUFFIT)
        putType("swf", MIME_APPLICATION_X_SHOCKWAVE_FLASH)
        putType("shar", MIME_APPLICATION_X_SHAR)
        putType("sh", MIME_APPLICATION_X_SH)
        putType("cdf", MIME_APPLICATION_X_NETCDF)
        putType("nc", MIME_APPLICATION_X_NETCDF)
        putType("latex", MIME_APPLICATION_X_LATEX)
        putType("skm", MIME_APPLICATION_X_KOAN)
        putType("skt", MIME_APPLICATION_X_KOAN)
        putType("skd", MIME_APPLICATION_X_KOAN)
        putType("skp", MIME_APPLICATION_X_KOAN)
        putType("js", MIME_APPLICATION_X_JAVASCRIPT)
        putType("hdf", MIME_APPLICATION_X_HDF)
        putType("gtar", MIME_APPLICATION_X_GTAR)
        putType("spl", MIME_APPLICATION_X_FUTURESPLASH)
        putType("dvi", MIME_APPLICATION_X_DVI)
        putType("dxr", MIME_APPLICATION_X_DIRECTOR)
        putType("dir", MIME_APPLICATION_X_DIRECTOR)
        putType("dcr", MIME_APPLICATION_X_DIRECTOR)
        putType("csh", MIME_APPLICATION_X_CSH)
        putType("cpio", MIME_APPLICATION_X_CPIO)
        putType("pack200", MIME_APPLICATION_X_PACK200)
        putType("bzip2", MIME_APPLICATION_X_BZIP2)
        putType("xz", MIME_APPLICATION_X_XZ)
        putType("pgn", MIME_APPLICATION_X_CHESS_PGN)
        putType("vcd", MIME_APPLICATION_X_CDLINK)
        putType("bcpio", MIME_APPLICATION_X_BCPIO)
        putType("rm", MIME_APPLICATION_VND_RNREALMEDIA)
        putType("ppt", MIME_APPLICATION_VND_MSPOWERPOINT)
        putType("mif", MIME_APPLICATION_VND_MIF)
        putType("grxml", MIME_APPLICATION_SRGS_XML)
        putType("gram", MIME_APPLICATION_SRGS)
        putType("smil", MIME_APPLICATION_RDF_SMIL)
        putType("smi", MIME_APPLICATION_RDF_SMIL)
        putType("rdf", MIME_APPLICATION_RDF_XML)
        putType("ogg", MIME_APPLICATION_X_OGG)
        putType("oda", MIME_APPLICATION_ODA)
        putType("dmg", MIME_APPLICATION_OCTET_STREAM)
        putType("lzh", MIME_APPLICATION_OCTET_STREAM)
        putType("so", MIME_APPLICATION_OCTET_STREAM)
        putType("lha", MIME_APPLICATION_OCTET_STREAM)
        putType("dms", MIME_APPLICATION_OCTET_STREAM)
        putType("bin", MIME_APPLICATION_OCTET_STREAM)
        putType("mathml", MIME_APPLICATION_MATHML_XML)
        putType("cpt", MIME_APPLICATION_MAC_COMPACTPRO)
        putType("hqx", MIME_APPLICATION_MAC_BINHEX40)
        putType("jnlp", MIME_APPLICATION_JNLP)
        putType("ez", MIME_APPLICATION_ANDREW_INSET)
        putType("txt", MIME_TEXT_PLAIN)
        putType("ini", MIME_TEXT_PLAIN)
        putType("c", MIME_TEXT_PLAIN)
        putType("h", MIME_TEXT_PLAIN)
        putType("cpp", MIME_TEXT_PLAIN)
        putType("cxx", MIME_TEXT_PLAIN)
        putType("cc", MIME_TEXT_PLAIN)
        putType("chh", MIME_TEXT_PLAIN)
        putType("java", MIME_TEXT_PLAIN)
        putType("csv", MIME_TEXT_PLAIN)
        putType("bat", MIME_TEXT_PLAIN)
        putType("cmd", MIME_TEXT_PLAIN)
        putType("asc", MIME_TEXT_PLAIN)
        putType("rtf", MIME_TEXT_RTF)
        putType("rtx", MIME_TEXT_RICHTEXT)
        putType("html", MIME_TEXT_HTML)
        putType("htm", MIME_TEXT_HTML)
        putType("zip", MIME_APPLICATION_ZIP)
        putType("rar", MIME_APPLICATION_X_RAR_COMPRESSED)
        putType("gzip", MIME_APPLICATION_X_GZIP)
        putType("gz", MIME_APPLICATION_X_GZIP)
        putType("tgz", MIME_APPLICATION_TGZ)
        putType("tar", MIME_APPLICATION_X_TAR)
        putType("ar", MIME_APPLICATION_X_AR)
        putType("gif", MIME_IMAGE_GIF)
        putType("jpeg", MIME_IMAGE_JPEG)
        putType("jpg", MIME_IMAGE_JPEG)
        putType("jpe", MIME_IMAGE_JPEG)
        putType("tiff", MIME_IMAGE_TIFF)
        putType("tif", MIME_IMAGE_TIFF)
        putType("png", MIME_IMAGE_PNG)
        putType("au", MIME_AUDIO_BASIC)
        putType("snd", MIME_AUDIO_BASIC)
        putType("wav", MIME_AUDIO_X_WAV)
        putType("mov", MIME_VIDEO_QUICKTIME)
        putType("qt", MIME_VIDEO_QUICKTIME)
        putType("mpeg", MIME_VIDEO_MPEG)
        putType("mpg", MIME_VIDEO_MPEG)
        putType("mpe", MIME_VIDEO_MPEG)
        putType("abs", MIME_VIDEO_MPEG)
        putType("doc", MIME_APPLICATION_MSWORD)
        putType("xls", MIME_APPLICATION_VND_MSEXCEL)
        putType("eps", MIME_APPLICATION_POSTSCRIPT)
        putType("ai", MIME_APPLICATION_POSTSCRIPT)
        putType("ps", MIME_APPLICATION_POSTSCRIPT)
        putType("pdf", MIME_APPLICATION_PDF)
        putType("exe", MIME_APPLICATION_OCTET_STREAM)
        putType("dll", MIME_APPLICATION_OCTET_STREAM)
        putType("class", MIME_APPLICATION_OCTET_STREAM)
        putType("jar", MIME_APPLICATION_JAVA_ARCHIVE)
        putType("apk", MIME_APPLICATION_ANDROID_PACKAGE)
        putType("7z", MIME_APPLICATION_7Z)
        putType("arj", MIME_APPLICATION_ARJ)

    }

    private fun putType(key: String, value: String) {
        if (mimeTypeMapping.put(key, value) != null) {
            throw IllegalArgumentException("Duplicated extension: $key")
        }
    }


    /**
     * Registers MIME type for provided extension. Existing extension type will be overriden.
     */
    fun registerMimeType(ext: String, mimeType: String) {
        mimeTypeMapping!![ext] = mimeType
    }

    /**
     * Returns the corresponding MIME type to the given extension.
     * If no MIME type was found it returns 'application/octet-stream' type.
     */
    fun getMimeType(ext: String): String {
        var mimeType = lookupMimeType(ext)
        if (mimeType == null) {
            mimeType = MIME_APPLICATION_OCTET_STREAM
        }
        return mimeType
    }

    /**
     * Simply returns MIME type or `null` if no type is found.
     */
    fun lookupMimeType(ext: String): String? {
        return mimeTypeMapping[ext.toLowerCase()]
    }

}