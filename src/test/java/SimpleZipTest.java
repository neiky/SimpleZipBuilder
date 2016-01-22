import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.Before;
import org.junit.Test;

import de.edag.util.SimpleZipBuilder;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SimpleZipTest
{
  File zip = new File ("testData/resources.zip");;
  
  @Before
  public void setup ()
  {
    zip.delete ();
  }
  
  @Test
  public void add_all_files_from_a_directory_to_a_zip_archive ()
      throws Exception
  {
    assertFalse ("Zip file should not exist in the beginning", zip.exists ());
    File source = new File ("testData/file1.txt");
    
    SimpleZipBuilder zipBuilder = new SimpleZipBuilder (zip);
    zipBuilder.addFilesToZip (source).buildZipFile ();
    
    assertTrue ("Expected to find the zip file ", zip.exists ());
    assertZipContent (zip, 1);
  }
  
  private void assertZipContent (File destination, int expectedCount) throws IOException
  {
    ZipFile zipFile = new ZipFile (destination);
    
    ZipArchiveEntry readme = zipFile.getEntry ("file1.txt");
    assertNotNull (readme);
    
    Enumeration<ZipArchiveEntry> entries = zipFile.getEntries ();
    int numberOfEntries = 0;
    while (entries.hasMoreElements ())
    {
      numberOfEntries++;
      entries.nextElement ();
    }
    assertThat (numberOfEntries, is (expectedCount));
    zipFile.close ();
  }
  
  @Test
  public void addTwoFilesToZip ()
      throws Exception
  {
    assertFalse ("Zip file should not exist in the beginning", zip.exists ());
    File source = new File ("testData/file1.txt");
    
    SimpleZipBuilder zipBuilder = new SimpleZipBuilder (zip);
    zipBuilder.addFilesToZip (source);
    
    source = new File ("testData/folder1/file2.txt");
    zipBuilder.addFilesToZip (source).buildZipFile ();

    ZipFile zipFile = new ZipFile (zip);
    assertNotNull (zipFile.getEntry ("file2.txt"));
    assertNotNull (zipFile.getEntry ("file1.txt"));
    
    assertTrue ("Expected to find the zip file ", zip.exists ());
    zipFile.close ();
    
    assertZipContent (zip, 2);
  }
  
  @Test
  public void addFileAndFolderToZip ()
      throws Exception
  {
    assertFalse ("Zip file should not exist in the beginning", zip.exists ());
    File source = new File ("testData/file1.txt");
    SimpleZipBuilder zipBuilder = new SimpleZipBuilder (zip);
    zipBuilder.addFilesToZip (source);

    source = new File ("testData/folder1");
    zipBuilder.addFilesToZip (source).buildZipFile ();

    ZipFile zipFile = new ZipFile (zip);
    assertNotNull (zipFile.getEntry ("folder1/file2.txt"));
    assertNotNull (zipFile.getEntry ("file1.txt"));
    
    assertTrue ("Expected to find the zip file ", zip.exists ());
    zipFile.close ();
    
    assertZipContent (zip, 2);
  }
}
