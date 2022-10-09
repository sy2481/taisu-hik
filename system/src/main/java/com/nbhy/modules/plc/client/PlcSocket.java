package com.nbhy.modules.plc.client;

import cn.hutool.core.util.StrUtil;
import com.nbhy.modules.hik.util.CrcUtils;
import com.nbhy.modules.plc.constant.PlcCommandConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static java.lang.Thread.sleep;

/**
 * 计算机连接网口
 *
 * @author lwt
 * @date 2018/07/15
 * @since 0.1.0
 */
public class PlcSocket {
    public enum CODE_TYPE {
        ASCII, //普通文本发送
        HEX    //十六进制发送
    }

    /**
     * ip
     */
    private String ip;
    /**
     * 端口
     */
    private int port;
    /**
     * 数据长度（对于定长值为0）
     */
    private int dataLength;
    /**
     * 编码方式
     */
    private String codeType;
    /**
     * 等待时间
     */
    private static final int DEFAULT_RANGE_FOR_SLEEP = 50;

    /**
     * 超时时间
     */
    private static final int DEFAULT_RANGE_TIME_OUT = 1000 * 60;

    /**
     * socket
     */
    private Socket socket = null;

    OutputStream os = null;
    OutputStreamWriter opsw = null;
    BufferedWriter bw = null;
    InputStream inputStream = null;

    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(PlcSocket.class);

    public PlcSocket(String ip, int port, int dataLength, String codeType) {
        this.ip = ip;
        this.port = port;
        this.dataLength = dataLength;
        this.codeType = codeType;

        try {
            socket = new Socket(ip, port);
            socket.setKeepAlive(true);
        } catch (Exception e) {
            logger.error("socket连接失败", ip + "--->" + port);
        }

    }

    public synchronized void close() {
        logger.info("socket close start");
        try {

            if (os != null) {
                os.close();
            }
            if (opsw != null) {
                opsw.close();
            }
            if (bw != null) {
                bw.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {

        }
        logger.info("socket close end");
    }

    public synchronized String sendComm(String command) {

        logger.info("start send message " + command);

        if (socket == null) {
//            int i = 0;
//            while (socket == null && i < 3) {
            try {
                socket = new Socket(ip, port);
                socket.setKeepAlive(true);
            } catch (IOException e) {
                logger.error("socket连接失败", ip + "--->" + port);
            }
            try {
                sleep(DEFAULT_RANGE_FOR_SLEEP);
            } catch (InterruptedException e) {
                logger.error("系统内部错误", e);
            }
//                i++;
//            }
        }

        //超时
        try {
            socket.setSoTimeout(DEFAULT_RANGE_TIME_OUT);
        } catch (SocketException e) {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            socket = null;
            return "";
        }

        //向网口发送数据

        try {
            os = socket.getOutputStream();
            if (codeType.equals(CODE_TYPE.HEX.name())) {
                os.write(CrcUtils.hexStringToByte(command));
            } else {
                opsw = new OutputStreamWriter(os);
                bw = new BufferedWriter(opsw);
                bw.write(command);
            }
            os.flush();
        } catch (Exception e) {
            logger.error("向PLC发送命令失败!", e);
            return "";
        }
//            }finally {
//                try {
//                    if(opsw!=null){
//                        opsw.close();
//                    }
//                    if(bw!=null){
//                        bw.close();
//                    }
//                }catch (Exception e){
//
//                }
//
//            }

        //读取数据
        String result = "";

        try {
            sleep(DEFAULT_RANGE_FOR_SLEEP);
            inputStream = socket.getInputStream();
//                if (dataLength == 0) {
//                    //定长有换行符
//            InputStreamReader ipsr = new InputStreamReader(inputStream);
//            BufferedReader br = new BufferedReader(ipsr);
//                               result = br.readLine();
//                } else {
//                    byte[] buffer = new byte[dataLength];
//                    int len = -1;
//                    if ((len = inputStream.read(buffer)) != -1) {
//                        result = CrcUtils.bytesToHexString(buffer);
//                    }
//                }
        } catch (Exception e) {
            logger.error("读取Socket信息失败", e);
        }
//            finally {
//                try {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                    if(os!=null) {
//                        os.close();
//                    }
//                }catch (Exception e){
//
//                }
//            }
        return result;
    }

    public boolean connect() {
        return true;
    }


    public static void sentMessage(String ip, String index) {
        if (ip != null && (ip.equals("") || (ip.equals("127.0.0.1")))) {
            return;
        }
        if (!index.equals("")) {
            PlcSocket plc = new PlcSocket(ip, 6000, 0, PlcSocket.CODE_TYPE.HEX.name());
            // byte[] bytes = CrcUtils.hexStringToByte(String.format(PlcCommandConstant.CLOSE_DOOR_COMMAND, index));
            plc.sendComm(String.format(PlcCommandConstant.CLOSE_DOOR_COMMAND, index));
//        plc.sendComm("02FF0A0000000000204D010000");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//        plc.sendComm("02FF0A0000000000204D010010");
            //byte[] bytes1 = CrcUtils.hexStringToByte(String.format(PlcCommandConstant.OPEN_DOOR_COMMAND, index));
            plc.sendComm(String.format(PlcCommandConstant.OPEN_DOOR_COMMAND, index));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//        plc.sendComm("02FF0A0000000000204D010000");
            plc.sendComm(String.format(PlcCommandConstant.CLOSE_DOOR_COMMAND, index));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            plc.close();
        }

    }


    public static void main(String[] args) {
        PlcSocket plc = new PlcSocket("192.168.1.202", 4000, 0, CODE_TYPE.HEX.name());
        System.out.println(plc.sendComm("03FF0A0000000000204D01000000"));

        System.out.println(plc.sendComm("03FF0A0000000000204D01000100"));
        try {
            Thread.sleep(5000);
        } catch (Exception e) {

        }
        System.out.println(plc.sendComm("03FF0A0000000000204D01000000"));
//        PlcSocket plc=new PlcSocket("192.168.70.152",6000,0,CODE_TYPE.HEX.name());
//        System.out.println(plc.sendComm("02FF0A0001000000204D010000"));
//        System.out.println(plc.sendComm("02FF0A0001000000204D010010"));
//        System.out.println(plc.sendComm("02FF0A0001000000204D010000"));
        plc.close();
//        try {
//            Thread.sleep(15000);
//        }catch (Exception e){
//
//        }
//        plc=new PlcSocket("192.168.70.152",6000,0,CODE_TYPE.HEX.name());
//        System.out.println(plc.sendComm("02FF0A0000000000204D010000"));
//        System.out.println(plc.sendComm("02FF0A0000000000204D010010"));
//        System.out.println(plc.sendComm("02FF0A0000000000204D010000"));
//        plc.close();
    }
}
