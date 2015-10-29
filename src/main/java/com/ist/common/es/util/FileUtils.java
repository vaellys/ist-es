/**
 * Copyright 2003 (C) PANLAB ，All Rights Reserved.
 * 日期         作者            动作
 * 2003-10-20   青蛙                     创建
 */
package com.ist.common.es.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FileUtils {
	/**
	* Logger for this class
	*/
	private static final Logger logger = Logger.getLogger(FileUtils.class);

	/** 文件缓冲区的长度 */
	private static int buffersize = 1024;

	/**
	 * 构造函数
	 */
	public FileUtils() {
	}

	/**
	 * 取得文件的真实路径
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件所在路径
	 */
	public synchronized static String getCanonicalFileName(String fileName) {
		try {
			File file = new File(fileName);
			return file.getCanonicalPath();// + File.separatorChar +
											// file.getName();
		} catch (Exception e) {

			logger.error("",e);
			return "";
		}
	}

	/**
	 * 从文件路径中读取文件名称
	 * 
	 * @param oldFileUrl
	 *            文件名含路径
	 * @return 文件名
	 */
	public synchronized static String getFilenameFromPath(String oldFileUrl) {
		int lastIndex = oldFileUrl.lastIndexOf("\\");
		if (lastIndex < 1 || lastIndex >= oldFileUrl.length() - 1)
			lastIndex = oldFileUrl.lastIndexOf("/");
		if (lastIndex >= 0 && lastIndex <= oldFileUrl.length())
			return oldFileUrl.substring(lastIndex + 1).trim();
		else
			return oldFileUrl;
	}

	/**
	 * 从文件路径中读取文件路径 add by wangjun
	 * 
	 * @param oldFileUrl
	 *            文件名含路径
	 * @return 文件路径
	 */
	public synchronized static String getPathFromPathName(String oldFileUrl) {
		int lastIndex = oldFileUrl.lastIndexOf("\\");
		if (lastIndex < 1 || lastIndex >= oldFileUrl.length() - 1)
			lastIndex = oldFileUrl.lastIndexOf("/");
		if (lastIndex >= 0 && lastIndex <= oldFileUrl.length())
			return oldFileUrl.substring(0, lastIndex).trim();
		else
			return oldFileUrl;
	}

	public static String getFileNameByUrl(String fileName) {
		fileName = getRealPath(fileName);
		int index = fileName.lastIndexOf("\\");
		if (index != -1) {
			return fileName.substring(index + 1);
		} else {
			index = fileName.lastIndexOf("/");
			if (index != -1) {
				return fileName.substring(index + 1);
			}
		}
		return fileName;
	}

	/**
	 * 得到文件或者路径的 相对于操作系统的 真实路径
	 * 
	 * @param s
	 * @return
	 */
	public static String getRealPath(String s) {

		try {
			String separator = System.getProperties().getProperty("file.separator");
			if ("/".equalsIgnoreCase(separator)) {
				s = s.replace('\\', '/');
			} else {
				s = s.replace('/', '\\');
			}
		} catch (Exception e) {

		}
		return s;
	}

	/**
	 * 根据绝对路径创建文件
	 * 
	 * @param oldFileUrl
	 *            文件名含路径
	 * @return 文件File
	 */
	public synchronized static File getFile(String filePathName) {
		String path = FileUtils.getPathFromPathName(filePathName);
		File filePath = new File(path);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		File file = new File(filePathName);
		return file;
	}

	/**
	 * 说明：将字符串写入一个文件中(static)
	 * 
	 * @param path
	 *            路径名称
	 * @param filename
	 *            读取的文件模版
	 * @param str
	 *            写入的字符串
	 */
	public static void writeFile(String path, String filename, String str) {
		try {
			File filePath = new File(path);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			File file = new File(path, filename);
			PrintWriter pw = new PrintWriter(new FileOutputStream(file));
			pw.println(str);
			pw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static boolean writeFile(String filename, InputStream in) {
		FileOutputStream output;
		try {
			output = new FileOutputStream(filename);
		} catch (Exception e) {

			logger.error("",e);
			return false;
		}
		byte[] buffer = new byte[buffersize];
		int len;
		try {
			while ((len = in.read(buffer, 0, buffersize)) > 0) {
				output.write(buffer, 0, len);
			}
			return true;
		} catch (Exception e) {

			logger.error("",e);
			return false;
		} finally {
			try {
				output.close();
			} catch (Exception e) {

				logger.error("",e);
			}
		}
	}

	/**
	 * 说明：读取文件内容(static)
	 * 
	 * @param filename
	 *            读取的文件名
	 * @return String 文件的字符串代码
	 */
	public static String readFile(String filename) {
		String return_str = "";
		try {
			FileReader fr = new FileReader(filename);
			LineNumberReader lr = new LineNumberReader(fr, 512);
			while (true) {
				String str = lr.readLine();
				if (str == null)
					break;
				return_str += str + "\n";
			}
			lr.close();
		} catch (FileNotFoundException e) {
			System.out.println("FILENAME:" + filename);
			System.out.println("File not found");
			// return_str="error1!";
		} catch (IOException e) {
			System.out.println("IO error");
			// return_str="error2!";
		}
		return return_str;
	}

	/**
	 * 说明：模板替换并返回(static)
	 * 
	 * @param filename
	 *            读取的文件模版
	 * @param tablename
	 *            替换列表
	 * @return String 模板替换出来的字符串
	 */
	// public static String replaceModel(String filename,Hashtable tablename){
	// String rstr=readFile(filename);
	// if(rstr.equals("error1!") || rstr.equals("error2!"))
	// return rstr;
	// for (Enumeration e=tablename.keys();e.hasMoreElements();){
	// String a=(String) e.nextElement();
	// String newstr=(String) tablename.get(a);
	// rstr=StringUtils.replaceString(rstr,a,newstr);
	// }
	// return rstr;
	// }
	@SuppressWarnings("rawtypes")
    public static String replaceModel(String filename, HashMap tablename) {
		String rstr = readFile(filename);
		if (rstr.equals("error1!") || rstr.equals("error2!"))
			return rstr;
		Iterator iterator = tablename.keySet().iterator();
		while (iterator.hasNext()) {
			String a = (String) iterator.next();
			String newstr = (String) tablename.get(a);
			rstr = StringUtils.replace(rstr, a, newstr);
		}
		return rstr;
	}

	/**
	 * 说明:单个文件的拷贝 文件属性在拷贝中丢失
	 * 
	 * @param from
	 *            原文件
	 * @param to
	 *            目标文件
	 * @return boolean true 成功 false 失败
	 */
	public static boolean copyFile(File from, File to) {
		FileInputStream input;
		FileOutputStream output;
		try {
			input = new FileInputStream(from);
			output = new FileOutputStream(to);
		} catch (Exception e) {

			logger.error("",e);
			return false;
		}
		byte[] buffer = new byte[buffersize];
		int len;
		try {
			while ((len = input.read(buffer, 0, buffersize)) > 0) {
				output.write(buffer, 0, len);
			}
			return true;
		} catch (Exception e) {

			logger.error("",e);
			return false;
		} finally {
			try {
				input.close();
				output.close();
			} catch (Exception e) {

				logger.error("",e);
			}
		}
	}

	/**
	 * 说明：将内容写到文件中,如果文件已经存在，则接到后面写
	 * 
	 * @param directory
	 *            路径
	 * @param filename
	 *            文件名
	 * @param content
	 *            内容
	 * @return boolean 写文件成功或失败
	 */
	public boolean contentAppendToFile(String directory, String filename, String content) {
		FileOutputStream fos = null;
		boolean flag = true;
		try {
			File objFile = new File(directory, filename);
			if (!objFile.exists()) {
				objFile.createNewFile();
				System.out.println("文件不存在!");
			}
			//
			byte[] b = content.getBytes();
			fos = new FileOutputStream(directory + "/" + filename, true);
			// System.out.println("开始写文件!");
			fos.write(b);
			// System.out.println("写文件结束!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			flag = false;
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
		return flag;
	}

	/**
	 * 删除文件
	 * 
	 * @param filename
	 *            文件名(要包括完整的路径)
	 */
	public static void deleteDir(String filename) {
		boolean isSucc = false;
		try {
			File file = new File(filename);
			if (file.exists()) {
				deleteFile(file);
				isSucc = true;
			}
		} catch (SecurityException e) {
			System.out.println("Delete File Fails:" + e.getMessage());
		} finally {
			if (isSucc) {
				System.out.println("删除成功!");
			} else {
				System.out.println("删除失败!");
			}
		}
	}

	public static void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
			file.delete();
		} else {
			file.delete();
		}
	}

	/**
	 * 判断文件路径是否存在
	 * 
	 * @param path
	 *            文件路径
	 * @return boolean
	 */
	public static boolean isExists(String path) {
		boolean exists = false;
		File file = new File(path);
		if (file.exists()) {
			exists = true;
		}

		return exists;
	}

	/**
	 * 取得某个文件路径下的所有文件列表
	 * 
	 * @param path
	 *            文件路径
	 * @param filter
	 *            文件过滤器
	 * @return List
	 * @throws Exception
	 */
	public static List<File> getListFiles(String path, FileFilter filter) throws Exception {
		List<File> list = new ArrayList<File>();

		File file = new File(path);

		if (!file.exists()) {
			return null;
		}
		if (!file.isDirectory()) {
			list.add(file);
			return list;
		}

		File files[] = file.listFiles(filter);
		for (int i = 0; i < files.length; i++) {
			File temp = files[i];
			if (temp.isFile()) {// 文件,直接添加该文件
				list.add(temp);
				continue;
			}
			List<File> fileList = getListFiles(temp, filter);// 目录,进行查找目录中的文件
			if (fileList != null) {
				list.addAll(fileList);
			}
		}

		return list;
	}

	/**
	 * 取得某个文件下的所有文件列表
	 * 
	 * @param file
	 *            文件
	 * @param filter
	 *            文件过滤器
	 * @return List
	 * @throws Exception
	 */
	public static List<File> getListFiles(File file, FileFilter filter) throws Exception {
		if (file == null)
			return null;
		List<File> list = new ArrayList<File>();

		if (!file.isDirectory()) {
			list.add(file);
			return list;
		}
		File files[] = file.listFiles(filter);
		for (int i = 0; i < files.length; i++) {
			File temp = files[i];
			if (temp.isFile()) {// 文件,直接添加该文件
				list.add(temp);
				continue;
			}
			List<File> fileList = getListFiles(temp, filter);// 目录,进行查找目录中的文件
			if (fileList != null) {
				list.addAll(fileList);
			}
		}

		return list;
	}

	public void createFile(String filePathAndName, String fileContent, String encoding) throws Exception {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			PrintWriter myFile = new PrintWriter(myFilePath, encoding);
			String strContent = fileContent;
			myFile.println(strContent);
			myFile.close();
		} catch (Exception e) {
			throw new Exception("创建文件操作出错" + e.getMessage());
		}
	}

	public static String read(String fileName, String encoding) {
		StringBuffer fileContent = new StringBuffer();
		BufferedReader br = null;
		try {
			FileInputStream fis = new FileInputStream(fileName);
			InputStreamReader isr = new InputStreamReader(fis, encoding);
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				fileContent.append(line);
				fileContent.append(System.getProperty(" line.separator "));
			}
		} catch (Exception e) {

		} finally{
		    if(null != br){
		        try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
		    }
		}
		return fileContent.toString();
	}

	public static void write(String fileContent, String fileName, String encoding) {
	    OutputStreamWriter osw = null;
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			osw = new OutputStreamWriter(fos, encoding);
			osw.write(fileContent);
			osw.flush();
		} catch (Exception e) {

		} finally{
		    if(null != osw){
		        try {
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
		    }
		}
	}

	/**
	 * 主函数
	 * 
	 * @param args
	 *            测试参数
	 */
	public static void main(String[] args) {
		FileUtils fUtils = new FileUtils();
		String aa = "asdfasdfasdf" + "\n";
		fUtils.contentAppendToFile("c:\\", "a.txt", aa);
		// System.out.println( fUtils.getFilenameFromPath("c:/a.txt"));
		try {
            List<File> listFiles = getListFiles("D:/path", null);
            for (File file : listFiles) {
                System.out.println(file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
