package Chapter15_IOStream;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileVisitorTest
{
	public void SerachFile() throws Exception
	{
		//D:\Programm\JAVA\Test\src\Test\Main.java
		Files.walkFileTree(Paths.get("D:", "Programm","JAVA","Test"), 
				new SimpleFileVisitor<Path>()
				{
					@Override
					public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException
					{
						System.out.println("正在访问"+file+"文件");
						if(file.endsWith("Main.java"))
						{
							System.out.println("已经找到"+file+"文件");
							return FileVisitResult.TERMINATE;
						}
						return FileVisitResult.CONTINUE;
					}
					@Override
					public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) throws IOException
					{
						System.out.println("正在访问"+dir+"路径");
						return FileVisitResult.CONTINUE;
					}
				}
		);
	}
}
