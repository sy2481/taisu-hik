//package com.nbhy.test;
//
//import gnu.io.CommPortIdentifier;
//import gnu.io.SerialPort;
//import gnu.io.SerialPortEvent;
//import gnu.io.SerialPortEventListener;
//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Enumeration;
//
///**
// * TODO
// *
// * @author linfeng
// * @date 2022/4/24 14:57
// */
//public class SerialPortManager {
//
//    private static String portName = "COM4"; //本地COM口
//    private static CommPortIdentifier commPortIdentifier;
//    private static SerialPort serialPort;
//    private static OutputStream out;
//    private static InputStream in;
//    private static int baud = 9600;
//
//    public static void main(String[] args) throws Exception {
//        //打开串口
//        commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
//        serialPort = (SerialPort) commPortIdentifier.open(portName,2000);
//        // 注册一个SerialPortEventListener事件来监听串口事件
//        serialPort.addEventListener(new SerialPortListener());
//        // 数据可用则触发事件
//        serialPort.notifyOnDataAvailable(true);
//        // 打开输入输出流
//        in = serialPort.getInputStream();
//        // 设置串口参数，波特率9600，8位数据位，1位停止位，无奇偶校验
//        serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//        System.out.println("打开串口成功");
//    }
//
//
//    public static class SerialPortListener implements SerialPortEventListener{
//        @Override
//        public void serialEvent(SerialPortEvent serialPortEvent) {
//            switch (serialPortEvent.getEventType()) {
//                case SerialPortEvent.DATA_AVAILABLE:
//                    //Data available at the serial port，端口有可用数据。读到缓冲数组，输出到终端
//                    System.out.println("端口有可用数据");
//                    try {
//                        if (in != null) {
//                            //缓冲区可自己修改
//                            byte[] cache = new byte[12];
//                            int availableBytes = 0;
//                            availableBytes = in.available();
//                            while (availableBytes > 0) {
//                                in.read(cache);
//                                String[] data = bytes2HexString(cache).split(" ");
//                                System.out.println(bytes2HexString(cache));
//                            }
//                        }
//                    }catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case SerialPortEvent.BI:
//                    //Break interrupt,通讯中断
//                    System.out.println("通讯中断");
//                    break;
//                case SerialPortEvent.OE:
//                    //Overrun error，溢位错误
//                    System.out.println("溢位错误");
//                    break;
//                case SerialPortEvent.FE:
//                    //Framing error，传帧错误
//                    System.out.println("传帧错误");
//                    break;
//                case SerialPortEvent.PE:
//                    //Parity error，校验错误
//                    System.out.println("校验错误");
//                    break;
//                case SerialPortEvent.CD:
//                    //Carrier detect，载波检测
//                    System.out.println("载波检测");
//                    break;
//                case SerialPortEvent.CTS:
//                    //Clear to send，清除发送
//                    System.out.println("清除发送");
//                    break;
//                case SerialPortEvent.DSR:
//                    // Data set ready，数据设备就绪
//                    System.out.println("数据设备就绪");
//                    break;
//                case SerialPortEvent.RI:
//                    //Ring indicator，响铃指示
//                    System.out.println("响铃指示");
//                    break;
//                case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
//                    // Output buffer is empty，输出缓冲区清空
//                    System.err.println("监听端口出现了异常");
//                    break;
//            }
//        }
//    }
//    /*
//     * 字节数组转16进制字符串
//     */
//    public static String bytes2HexString(byte[] b) {
//        String r = "";
//        for (int i = 0; i < b.length; i++) {
//            String hex = Integer.toHexString(b[i] & 0xFF);
//            if (hex.length() == 1) {
//                hex = '0' + hex;
//            }
//            r += hex.toUpperCase()+" ";
//        }
//        return r;
//    }
//
//    /**
//     * 获取系统com端口
//     * @return
//     */
//    public static String getSystemSerialPort(){
//        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
//        String portName ="";
//        while (portList.hasMoreElements()) {
//            portName = portList.nextElement().getName();
//            System.out.println(portName);
//        }
//        return portName;
//    }
//}
