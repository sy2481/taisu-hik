package com.nbhy.modules.hik.util;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: yyf
 * @Date: Created in 9:17 2022/1/20
 * @ClassName: CrcUtils
 * @Description:
 */
@Slf4j
public class CrcUtils {
    public static String CRC_16(byte[] bytes) {
        int i, j, lsb;
        int h = 0xffff;
        for (i = 0; i < bytes.length; i++) {
            h ^= bytes[i];
            for (j = 0; j < 8; j++) {
                lsb = h & 0x0001; // 取 CRC 的移出位
                h >>= 1;
                if (lsb == 1) {
                    h ^= 0x8408;
                }
            }
        }
        h ^= 0xffff;
        return Integer.toHexString(h).toUpperCase();
    }

    public static byte[] hexStringToByte(String hex) {
        log.info("需要转换的指令为>>>>>>>>>>{}",hex);
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static String bytesToHexString(ByteBuf buffer) {
        final int length = buffer.readableBytes();
        StringBuffer sb = new StringBuffer(length);
        String sTmp;

        for (int i = 0; i < length; i++) {
            byte b = buffer.readByte();
            sTmp = Integer.toHexString(0xFF & b);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }
        return sb.toString();
    }


    public static String bytesToHexString(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length);
        String sTmp;

        for (int i = 0; i < data.length; i++) {
            sTmp = Integer.toHexString(0xFF & data[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }
        return sb.toString();
    }
}
