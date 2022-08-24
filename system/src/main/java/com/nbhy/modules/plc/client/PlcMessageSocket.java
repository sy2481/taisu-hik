package com.nbhy.modules.plc.client;

import cn.hutool.core.util.StrUtil;
import com.nbhy.modules.hik.constant.SubtitleMachineConstant;
import com.nbhy.modules.hik.util.CrcUtils;
import com.nbhy.modules.hik.util.SubtitleMachineUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static java.lang.Thread.sleep;

/**
 * description :
 *
 * @author : SmallBean-Wang
 * @create : 2022/5/30 10:09
 */
public class PlcMessageSocket {
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
	OutputStreamWriter opsw=null;
	BufferedWriter bw=null;
	InputStream inputStream = null;

	/**
	 * logger
	 */
	private final Logger logger = LoggerFactory.getLogger(PlcSocket.class);

	public PlcMessageSocket(String ip, int port, int dataLength, String codeType) {
		this.ip = ip;
		this.port = port;
		this.dataLength = dataLength;
		this.codeType = codeType;

		try {
			socket = new Socket(ip, port);
			socket.setKeepAlive(true);
		} catch (Exception e) {
			logger.error("socket message 连接失败", ip+"--->"+port);
		}

	}

	public synchronized void close(){
		logger.info("socket close start");
		try {

			if(os!=null){
				os.close();
			}if(opsw!=null){
				opsw.close();
			}
			if(bw!=null){
				bw.close();
			}if(inputStream!=null){
				inputStream.close();
			}
			if(socket!=null){
				socket.close();
			}
		}catch (Exception e){

		}
		logger.info("socket close end");
	}

	public synchronized String sendComm(String command) {

		logger.info("start send message "+command);
		if (socket == null) {
//			int i = 0;
//			while (socket == null && i < 3) {
				try {
					socket = new Socket(ip, port);
					socket.setKeepAlive(true);
				} catch (IOException e) {
					logger.error("socket message 连接失败", ip+"--->"+port);
				}
				try {
					sleep(DEFAULT_RANGE_FOR_SLEEP);
				} catch (InterruptedException e) {
					logger.error("系统内部错误", e);
				}
				//i++;
//			}
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
			if (codeType.equals(PlcSocket.CODE_TYPE.HEX.name())) {
				os.write(SubtitleMachineUtil.getCommand(command));
			} else {
//				opsw = new OutputStreamWriter(os);
//				bw = new BufferedWriter(opsw);
//				bw.write(command);
			}
			os.flush();
		} catch (Exception e) {
			logger.error("向PLC发送命令失败!", e);
			return "";
		}

		//读取数据
		String result = "";

		try {
			sleep(DEFAULT_RANGE_FOR_SLEEP);
			inputStream = socket.getInputStream();
//                if (dataLength == 0) {
//                    //定长有换行符
			//InputStreamReader ipsr = new InputStreamReader(inputStream);
			//BufferedReader br = new BufferedReader(ipsr);
			//                   result = br.readLine();
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
		return result;
	}

	public synchronized String sendCommLine() {

		logger.info("start send message ");

		if (socket == null) {
//			int i = 0;
//			while (socket == null && i < 3) {
				try {
					socket = new Socket(ip, port);
					socket.setKeepAlive(true);
				} catch (IOException e) {
					logger.error("socket连接失败", e);
				}
				try {
					sleep(DEFAULT_RANGE_FOR_SLEEP);
				} catch (InterruptedException e) {
					logger.error("系统内部错误", e);
				}
//				i++;
//			}
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
			if (codeType.equals(PlcSocket.CODE_TYPE.HEX.name())) {
//				os.write(CrcUtils.hexStringToByte(command));
				os.write(SubtitleMachineConstant.RESTORE_LINE_NUMBER);
			} else {
//


//				opsw = new OutputStreamWriter(os);
//				bw = new BufferedWriter(opsw);
//				bw.write(command);
			}
			os.flush();
		} catch (Exception e) {
			logger.error("向PLC发送命令失败!", e);
			return "";
		}


		//读取数据
		String result = "";

		try {
			sleep(DEFAULT_RANGE_FOR_SLEEP);
			inputStream = socket.getInputStream();
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


	public static void sentMessage(String ip,String message){
		PlcMessageSocket plc=new PlcMessageSocket(ip,6000,0,CODE_TYPE.HEX.name());
		plc.sendCommLine();
//		String message="中文 english";
		String[] messages = StrUtil.split(message, 5);
		for (int i = 0; i < messages.length; i++) {
			plc.sendComm(messages[i]);
			//如果没有字符，退出循环
			if(i == (message.length() -1) ){
				continue;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		plc.sendComm("");
		plc.close();
		try {
			Thread.sleep(150);
		}catch (Exception e){

		}

	}


	public static void main(String[] args) {
//		PlcMessageSocket plc=new PlcMessageSocket("192.168.70.86",6000,0,CODE_TYPE.HEX.name());
//		plc.sendCommLine();
//		String message="中文 english";
//		String[] messages = StrUtil.split(message, 5);
//		for (int i = 0; i < messages.length; i++) {
//			plc.sendComm(messages[i]);
//			//如果没有字符，退出循环
//			if(i == (message.length() -1) ){
//				continue;
//			}
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//
//
//		plc.sendComm("");
//		plc.close();
//		try {
//			Thread.sleep(150);
//		}catch (Exception e){
//
//		}

		PlcMessageSocket.sentMessage("192.168.70.201", "綁卡成功綁卡成功");

	}


}
