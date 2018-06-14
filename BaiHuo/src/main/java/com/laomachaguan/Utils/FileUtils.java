package com.laomachaguan.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class FileUtils {

	private static final String TAG = "FileUtils";
	public static final  String TEMPPAH = Environment.getExternalStorageDirectory()+File.separator
			+Constants.cacheO+ "/temp/";

	public static void saveBitmap(Bitmap bm, String picName) {
		System.out.println("-----------------------------");
		try {
			if (!isFileExist(picName)) {
				System.out.println("创建文件");
				File tempf = createSDDir(picName);
			}
			File f = new File(TEMPPAH, picName);
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 30, out);
			out.flush();
			out.close();
			bm.recycle();
			bm=null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(TEMPPAH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			Log.e(TAG, "createSDDir: "+dir.getAbsolutePath() );
			Log.e(TAG, "createSDDir: "+dir.mkdirs() );
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(TEMPPAH + fileName);
		file.isFile();
		System.out.println(file.exists());
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(TEMPPAH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(TEMPPAH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); 
			else if (file.isDirectory())
				deleteDir(); 
		}
		dir.delete();
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * 请空文件夹
	 *
	 * @param dir
	 */
	public static void deleteFile(File dir) {
		if(dir.isDirectory()){
			String list[] = dir.list();
			for (String f : list) {
				File file = new File(dir.getAbsolutePath() + "/" + f);
				if (file.isDirectory()) {
					deleteFile(file);
				} else {
					file.delete();
				}
			}
		}else if(dir.isFile()){
			dir.delete();
		}

	}

	/**
	 * 获取文件夹大小
	 *
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File dir) throws Exception
	//获取文件夹大小
	{
		long size = 0;
		if(dir.isDirectory()){
			File flist[] = dir.listFiles();
			for (int i = 0; i < flist.length; i++) {
				File file = flist[i];
				if (file.isDirectory()) {
					size = size + getFileSize(file);
				} else {
					size = size + file.length();
				}
			}
		}else if(dir.isFile()){
			size=dir.length();
		}


		return size;
	}

	/**
	 * 速度最快的复制文件方式
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			inputChannel.close();
			outputChannel.close();
		}
	}
	/**
	 * 数据存放在本地
	 *
	 * @param tArrayList
	 */
	public static void saveStorage2SDCard(Context context,ArrayList tArrayList, String fileName) {

		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		FileInputStream fileInputStream = null;
		try {
			File f=new File(context.getExternalCacheDir()+File.separator+fileName);
			fileOutputStream = new FileOutputStream(f);  //新建一个内容为空的文件
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(tArrayList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (objectOutputStream != null) {
			try {
				objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (fileOutputStream != null) {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取本地的List数据
	 *
	 * @return
	 */
	public static ArrayList<String> getStorageStringEntities(Context context,String fileName) {
		ObjectInputStream objectInputStream = null;
		FileInputStream fileInputStream = null;
		ArrayList<String > savedArrayList =null;
		try {
			File file =new File(context.getExternalCacheDir()+File.separator+fileName);
			fileInputStream = new FileInputStream(file.toString());
			objectInputStream = new ObjectInputStream(fileInputStream);
			savedArrayList = (ArrayList<String >) objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return savedArrayList;
	}
	/**
	 * 获取本地的int数据
	 *
	 * @return
	 */
	public static ArrayList<Integer> getStorageIntEntities(Context context,String fileName) {
		ObjectInputStream objectInputStream = null;
		FileInputStream fileInputStream = null;
		ArrayList<Integer > savedArrayList =null;
		try {
			File file =new File(context.getExternalCacheDir()+File.separator+fileName);
			fileInputStream = new FileInputStream(file.toString());
			objectInputStream = new ObjectInputStream(fileInputStream);
			savedArrayList = (ArrayList<Integer >) objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return savedArrayList;
	}
	/**
	 * 获取本地数据
	 *
	 * @return
	 */
	public static ArrayList getStorageEntities(Context context,String fileName) {
		ObjectInputStream objectInputStream = null;
		FileInputStream fileInputStream = null;
		ArrayList savedArrayList =null;
		try {
			File file =new File(context.getExternalCacheDir()+File.separator+fileName);
			fileInputStream = new FileInputStream(file.toString());
			objectInputStream = new ObjectInputStream(fileInputStream);
			savedArrayList = (ArrayList) objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return savedArrayList;
	}
	/**
	 * 获取本地的List数据
	 *
	 * @return
	 */
	public static ArrayList<HashMap<String,Object>> getStorageMapEntities(Context context, String fileName) {
		ObjectInputStream objectInputStream = null;
		FileInputStream fileInputStream = null;
		ArrayList<HashMap<String,Object> > savedArrayList=null;
		try {
			File file =new File(context.getExternalCacheDir()+File.separator+fileName);
			fileInputStream = new FileInputStream(file.toString());
			objectInputStream = new ObjectInputStream(fileInputStream);
			savedArrayList = (ArrayList<HashMap<String,Object>>) objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return savedArrayList;
	}
}