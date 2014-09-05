package com.openfarmanager.android.utils;

import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * A set of static methods for IPv4 addreses processing, conversion and validation.
 *
 * @author Vlad Namashko (bav)
 */
public class NetworkUtil {
    public static final String[] tunnelSubnets = {"192.168.0.0/16", "172.16.0.0/12", "10.0.0.0/8"};
    public static final String ipPattern = "(\\d{1,3}\\.){3}\\d{1,3}";

    /**
     * Converts IP address from <code>int</code> to <code>String</code>
     * representation.
     *
     * @param address given IP address
     * @return IP address as string like "x.x.x.x"
     */
    public static String ipIntToStringRevert(int address) {
        return String.format("%d.%d.%d.%d", address & 0xff, address >> 8 & 0xff,
                address >> 16 & 0xff, address >> 24 & 0xff);
    }

    public static String ipIntToString(int address) {
        StringBuilder sb = new StringBuilder(15);
        sb.append((address >>> 24) & 0xFF).append('.')
                .append((address >>> 16) & 0xFF).append('.')
                .append((address >>> 8) & 0xFF).append('.')
                .append((address) & 0xFF);
        return sb.toString();
    }

    public static String revertIpAddress(String ipAddress) throws ParseException {
        if (null == ipAddress) {
            throw new ParseException("Can't parse internet address", 0);
        }

        String[] octets = ipAddress.split("[.]");
        if (4 != octets.length) {
            throw new ParseException("Can't parse internet address", 0);
        }

        String revertedIp = "";

        for (int i = 3; i >= 0; i--) {
            revertedIp += octets[i];

            if (i > 0) {
                revertedIp += ".";
            }

        }

        return revertedIp;
    }

    public static byte[] ipIntToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    /**
     * Converts IP address from <code>byte[]</code> to <code>String</code>
     * representation.
     *
     * @param address given IP address
     * @return IP address as string like "x.x.x.x"
     */
    public static String ipBytesToString(byte[] address) {
        return ((((int) address[0]) & 0xFF) + "." +
                (((int) address[1]) & 0xFF) + "." +
                (((int) address[2]) & 0xFF) + "." +
                (((int) address[3]) & 0xFF));
    }

    /**
     * Converts IP address from <code>byte</code> array to <code>int</code>
     * representation.
     *
     * @param _addr given IP address
     * @return IP address as int value
     */
    public static int ipBytesToInt(byte[] _addr) {
        int address;
        address = _addr[3] & 0xFF;
        address |= ((_addr[2] << 8) & 0xFF00);
        address |= ((_addr[1] << 16) & 0xFF0000);
        address |= ((_addr[0] << 24) & 0xFF000000);
        return address;
    }

    /**
     * Converts IP address from <code>String</code> to <code>byte</code> array
     * representation.
     *
     * @param address given IP address as string like "x.x.x.x"
     * @return IP address as four bytes
     * @throws java.text.ParseException if string cannot be parsed
     */
    public static byte[] ipStringToBytes(String address)
            throws ParseException {
        if (address == null)
            throw new ParseException("Can't parse internet address", 0);

        StringTokenizer st = new StringTokenizer(address, ".");
        int i = 0;
        byte[] ip = new byte[4];
        try {
            while (st.hasMoreTokens()) {
                int a = Integer.parseInt(st.nextToken());
                if (a < 0 || a > 255) {
                    throw new ParseException("Can't parse internet address", i);
                }
                ip[i++] = (byte) a;
            }
        } catch (Exception e) {
            throw new ParseException("Can't parse internet address", i);

        }
        return ip;
    }

    /**
     * Converts IP address from <code>String</code> to <code>int</code>
     * representation.
     *
     * @param address given IP address as string like "x.x.x.x"
     * @return IP address as int value
     * @throws java.text.ParseException is string cannot be parsed
     */
    public static int ipStringToInt(String address) throws ParseException {
        return ipBytesToInt(ipStringToBytes(address));
    }

    /**
     * Splits string representation of IPv4 address it array of integer numbers.
     *
     * @param ip IP address as string
     * @return array of integers numbers
     * @throws java.text.ParseException if string cannot be parsed
     */
    public static int[] ipStringToIntArray(String ip) throws ParseException {
        String[] values = ip.split("[.]");
        int[] intValues = new int[4];

        int i = 0;
        try {
            for (String str : values) {
                intValues[i++] = Integer.parseInt(str, 10);
            }
        } catch (Exception e) {
            throw new ParseException("Can't parse internet address", 0);
        }

        if (i < 4) // To short internet address
            throw new ParseException("Invalid internet address", 0);

        return intValues;
    }

    /**
     * Corrects string representation of IP address if necessary, removing redundant leading zeros in each number and
     * checking each number to be between 0 and 255
     *
     * @param ip address as string
     * @return corrected IP address as string
     */
    public static String checkIp(String ip) {
        StringTokenizer st = new StringTokenizer(ip, ".");
        boolean first = true;
        String newIp = "";
        try {
            while (st.hasMoreTokens()) {
                int value = Integer.parseInt(st.nextToken(), 10);
                if (value < 0 || value > 255) {
                    return null;
                }

                if (first) {
                    newIp += Integer.toString(value);
                    first = false;
                } else {
                    newIp += '.' + Integer.toString(value);
                }
            }
        } catch (Exception e) {
            return null;
        }

        return newIp;
    }

    /**
     * Corrects string representation of IP address if necessary, removing redundant leading zeros in each number.
     * Also checks equality of ip address and some reserved ip addresses.
     *
     * @param ip IP address as string
     * @return corrected IP address as string
     */
    public static String correctIp(String ip) {
        ip = ip.trim();

        String newIp = checkIp(ip);

        if (newIp == null || newIp.equals("0.0.0.0") || newIp.equals("255.255.255.255") || !newIp.matches(ipPattern)) {
            return null;
        }

        return newIp;
    }

    /**
     * Checks the correctness of IP address mask for local subnet.
     *
     * @param mask IP mask to check
     * @return true if IP mask is corect
     */
    public static boolean isCorrectMask(String mask) {
        try {
            ipStringToIntArray(mask);
        } catch (ParseException e) {
            return false;
        }

        String[] masks = {"0.0.0.0", "128.0.0.0", "192.0.0.0", "224.0.0.0", "240.0.0.0", "248.0.0.0",
                "252.0.0.0", "254.0.0.0", "255.0.0.0", "255.128.0.0", "255.192.0.0", "255.224.0.0",
                "255.240.0.0", "255.248.0.0", "255.252.0.0", "255.254.0.0", "255.255.0.0",
                "255.255.128.0", "255.255.192.0", "255.255.224.0", "255.255.240.0", "255.255.248.0",
                "255.255.252.0", "255.255.254.0", "255.255.255.0", "255.255.255.128", "255.255.255.192",
                "255.255.255.224", "255.255.255.240", "255.255.255.248", "255.255.255.252",
                "255.255.255.254", "255.255.255.255"
        };

        try {
            for (String mask1 : masks) {
                if (correctIp(mask).equals(mask1)) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }

    public static boolean isValidIp(String address) {
        int[] intValues;

        try {
            intValues = ipStringToIntArray(address);
        } catch (ParseException e) {
            return false;
        }

        for (int quad : intValues) {
            if (quad < 0 || quad > 255) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks the correctness of IP address depending of local subnetnet class which is determining by first one ore two
     * numbers of IP address.
     *
     * @param ip IP to check
     * @return true if IP is correct
     */
    public static boolean isCorrectIp(String ip) {
        int[] intValues;

        try {
            intValues = ipStringToIntArray(ip);
        } catch (ParseException e) {
            return false;
        }

        if ((intValues[0] == 10) &&
                (intValues[1] >= 0 && intValues[1] <= 255) &&
                (intValues[2] >= 0 && intValues[2] <= 255) &&
                (intValues[3] >= 0 && intValues[3] <= 255)) {
            return true;
        } else if ((intValues[0] == 172) && (intValues[1] >= 16 && intValues[1] <= 31) &&
                (intValues[2] >= 0 && intValues[2] <= 255) &&
                (intValues[3] >= 0 && intValues[3] <= 255)) {
            return true;
        } else if ((intValues[0] == 192) && (intValues[1] == 168) &&
                (intValues[2] >= 0 && intValues[2] <= 255) &&
                (intValues[3] >= 0 && intValues[3] <= 255)) {
            return true;
        }

        return false;
    }

    public static String getTunnelSubnet(String ip) {
        if (!isCorrectIp(ip)) {
            throw new IllegalArgumentException("String [" + ip + "] is not correct virtual subnet IP address");
        }
        if (ip.startsWith("192")) {
            return tunnelSubnets[0];
        } else if (ip.startsWith("172")) {
            return tunnelSubnets[1];
        } else {
            return tunnelSubnets[2];
        }
    }

    /**
     * Convert CIDR subnet mask to quad form in network byte order
     *
     * @param maskLen CIDR subnet mask
     * @return subnet mask in network byte order
     */
    public static byte[] cidrToQuad(int maskLen) {

        if (maskLen < 0 || maskLen > 32) {
            throw new IllegalArgumentException("CIDR mask should be in range 0-32.");
        }
        byte[] addr = new byte[4];

        int mask = (0xFFFFFFFF << (32 - maskLen));
        addr[0] = (byte) ((mask >>> 24) & 0xFF);
        addr[1] = (byte) ((mask >>> 16) & 0xFF);
        addr[2] = (byte) ((mask >>> 8) & 0xFF);
        addr[3] = (byte) (mask & 0xFF);

        return addr;
    }

    /**
     * Convert network mask in a quad form to a CIDR mask length
     *
     * @param mask network mask in a quad form
     * @return CIDR mask length
     */
    public static int quadToCidr(byte mask[]) {
        if (mask.length != 4) {
            throw new IllegalArgumentException("Mask should be 4 bytes long.");
        }

        int maskLen = 0;
        int intMask = NetworkUtil.ipBytesToInt(mask);
        // check either provided mask is contiguous
        // and calculate mask length
        for (int j = 0; j < 32 && intMask < 0; j++) {
            maskLen++;
            intMask <<= 1;
        }
        if (intMask > 0) {
            throw new IllegalArgumentException("The subnet mask has to be contiguous.");
        }

        return maskLen;
    }

    public static byte[] networkStartIp(byte[] ipAddress, byte[] mask) {
        return AND(ipAddress, mask);
    }

    public static byte[] networkStartIp(String ipAddress, String mask) throws ParseException {
        return networkStartIp(NetworkUtil.ipStringToBytes(ipAddress), NetworkUtil.ipStringToBytes(mask));
    }

    public static int numberOfAddressesInMask(String mask) throws ParseException {
        int len = NetworkUtil.quadToCidr(NetworkUtil.ipStringToBytes(mask));
        return (int) Math.pow(2, 32 - len);
    }

    public static int numberOfAddressesInMask(byte mask[]) {
        int len = NetworkUtil.quadToCidr(mask);
        return (int) Math.pow(2, 32 - len);
    }

    public static byte[][] allAddressesInSubnet(byte[] startIp, int numberOfAddressesInMask) throws ParseException {

        byte[][] addresses = new byte[numberOfAddressesInMask][];

        int start = ipBytesToInt(startIp);

        for (int i = 0; i < numberOfAddressesInMask; i++) {
            addresses[i] = ipStringToBytes(ipIntToString(start++));
        }

        return addresses;
    }

    public static String[] allStringAddressesInSubnet(byte[] startIp, int numberOfAddressesInMask) throws ParseException {

        String[] addresses = new String[numberOfAddressesInMask];

        int start = ipBytesToInt(startIp);

        for (int i = 0; i < numberOfAddressesInMask; i++) {
            addresses[i] = ipIntToString(start++);
        }

        return addresses;
    }

    public static byte[][] allAddressesInSubnet(String ipAddress, String mask) throws ParseException {

        byte[] startIp = networkStartIp(ipAddress, mask);
        int numberOfAddressesInMask = numberOfAddressesInMask(mask);

        return allAddressesInSubnet(startIp, numberOfAddressesInMask);
    }

    public static byte[][] allAddressesInSubnet(int ipAddress, int mask) throws ParseException {
        return allAddressesInSubnet(ipIntToString(ipAddress), ipIntToString(mask));
    }

    public static String[] allStringAddressesInSubnet(String ipAddress, String mask) throws ParseException {

        byte[] startIp = networkStartIp(ipAddress, mask);
        int numberOfAddressesInMask = numberOfAddressesInMask(mask);

        return allStringAddressesInSubnet(startIp, numberOfAddressesInMask);
    }

    public static String[] allStringAddressesInSubnet(int ipAddress, int mask) throws ParseException {
        return allStringAddressesInSubnet(ipIntToString(ipAddress), ipIntToString(mask));
    }

    public static String[] allStringAddressesInSubnet(byte[] ipAddress, byte[] mask) throws ParseException {
        return allStringAddressesInSubnet(ipBytesToString(ipAddress), ipBytesToString(mask));
    }

    public static byte[] AND(byte[] a1, byte[] a2) {
        byte[] r = new byte[a1.length];
        for (int i = 0; i < a1.length; i++) {
            r[i] = new Integer(a1[i] & a2[i]).byteValue();
        }
        return r;
    }

    public static byte[] XOR(byte[] a1, byte[] a2) {
        byte[] r = new byte[a1.length];
        for (int i = 0; i < a1.length; i++) {
            r[i] = new Integer(a1[i] ^ a2[i]).byteValue();
        }
        return r;
    }

    public static byte[] NOT(byte[] a) {
        byte[] r = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            r[i] = new Integer(~a[i]).byteValue();
        }
        return r;
    }
}
