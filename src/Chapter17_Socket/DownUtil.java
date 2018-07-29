
package Chapter17_Socket;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Liu_f
 * @Time 2018.7.21
 */
public class DownUtil
{
	//����������Դ��·��
	private String path;
	//ָ�������ļ������λ��
	private String targetFile;
	private int threadNum = 0;
	private DownThread[] threads;
	private int fileSize;
	
 	private class DownThread extends Thread
	{
		//��ǰ�߳����ڵ�
		private int startpos;
		//���嵱ǰ�߳���Ҫ�������ļ��Ĵ�С
		private int currentPartSize;
		//��ǰ�߳���Ҫ���ص��ļ���
		private RandomAccessFile currentPart;
		//���߳���Ҫ���ص��ֽ���
		public int length;
		public DownThread(int startpos,int currentPartSize,RandomAccessFile currentPart)
		{
			this.startpos = startpos;
			this.currentPartSize = currentPartSize;
			this.currentPart = currentPart;
		}
		@SuppressWarnings("deprecation")
		public void run()
		{
			try 
			{
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(5*1000);
				conn.setRequestMethod("GET");
				//conn.setRequestProperty("Accept", "");
				conn.setRequestProperty("Accept-Language", "zh-CN");
				conn.setRequestProperty("Charset", "UTF-8");
				conn.setRequestProperty("Connection", "Keep-Alive");
				
				InputStream inStream = conn.getInputStream();
				inStream.skip(this.startpos);
				byte[] buffer = new byte[1024];
				int hasRead = 0;
				
				//��ȡ�������ݲ�д���ļ���
				while(length < currentPartSize && ((hasRead = inStream.read(buffer))!=-1) )
				{
					currentPart.write(buffer,0,hasRead);
					length+=hasRead;
				}
				
				currentPart.close();
				inStream.close();	
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	//�������ؽ�����
	public double getCompleteRate()
	{
		int sumSize = 0;
		for(int i = 0;i<threadNum;i++)
		{
			sumSize +=threads[i].length;
		}
		
		return sumSize*1.0/fileSize;
	}
	
	//���������̶߳���
	public DownUtil(String path,String targetFile,int threadNum)
	{
		this.path = path;
		this.targetFile = targetFile;
		this.threadNum = threadNum;
		
		threads= new DownThread[threadNum];
	}
	
	public void download()throws Exception
	{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(5*1000);
		conn.setRequestMethod("GET");
		//conn.setRequestProperty("Accept", "");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Connection", "Keep-Alive");
		
		fileSize = conn.getContentLength();
		conn.disconnect();
		
		int currentPartSize = fileSize/threadNum +1;
		
		RandomAccessFile file= new RandomAccessFile(targetFile, "rw");
		file.setLength(fileSize);
		file.close();
		for(int i=0; i<threadNum;i++)
		{
			int startPos = i*currentPartSize;
			RandomAccessFile currentPart = new RandomAccessFile(targetFile, "rw");
			currentPart.seek(startPos);
			threads[i] = new DownThread(startPos, currentPartSize, currentPart);
			threads[i].start();
		}
	}
	
	public static void main(String args[])throws Exception
	{
		String downLoadPath="http://www.crazyit.org/data/attachment/forum/month_1505/1505211840159dc4881279476c.png";
		final DownUtil downUtil = new DownUtil(downLoadPath, "favourate1.png", 4);
		downUtil.download();
		new Thread(
				()->
				{
					while(downUtil.getCompleteRate()<1)
					{
						System.out.println("����ɣ�"+downUtil.getCompleteRate());
						try
						{
							Thread.sleep(1000);
						}
						catch(Exception e) {}
					}
					System.out.println("�Ѿ�������ɣ�");
				}
				).start();
	}
}
