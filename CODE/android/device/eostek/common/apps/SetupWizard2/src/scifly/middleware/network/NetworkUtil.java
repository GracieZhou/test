
package scifly.middleware.network;

public class NetworkUtil {

    public static int netmaskToPrefixLength(String netmask) {
        String[] tmp = netmask.split("\\.");
        int cnt = 0;
        for (String cell : tmp) {
            int i = Integer.parseInt(cell);
            cnt += Integer.bitCount(i);
        }

        return cnt;
    }

    public static String prefixToNetmask(int prefixLength) {
        int value = 0xffffffff << (32 - prefixLength);
        int netmask = Integer.reverseBytes(value);

        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(netmask & 0xff));
        sb.append('.');
        sb.append(String.valueOf((int) ((netmask >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((netmask >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((netmask >> 24) & 0xff)));

        return sb.toString();
    }
}
