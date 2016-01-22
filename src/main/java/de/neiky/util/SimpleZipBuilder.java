package de.neiky.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleZipBuilder
{
  private static final Logger LOGGER = LogManager.getLogger(SimpleZipBuilder.class);
  
  private File zip;
  private ArchiveOutputStream archive;
  private FileOutputStream archiveStream;

  public SimpleZipBuilder(File zip) throws FileNotFoundException, ArchiveException {
    this.zip = zip;
    this.archiveStream = new FileOutputStream (zip);
    this.archive = new ArchiveStreamFactory ()
        .createArchiveOutputStream (ArchiveStreamFactory.ZIP, archiveStream);
    
    LOGGER.debug ("Initiated new ZIP file {}.", this.zip);
  }
  
  
  /**
   * Add all files from the source directory to the destination zip file
   *
   * @param source
   *          the directory with files to add
   * @param zipFile
   *          the zip file that should contain the files
   * @throws IOException
   *           if the io fails
   * @throws ArchiveException
   *           if creating or adding to the archive fails
   */
  public SimpleZipBuilder addFilesToZip (File source)
      throws IOException, ArchiveException
  {
    LOGGER.info ("Adding {} to archive {}.", source, this.zip);
    
    if (source.isDirectory ())
    {
      Collection<File> fileList = FileUtils.listFiles (source, null, true);
      
      for (File file : fileList)
      {
        String entryName = getEntryName (source.toPath ().getParent ().toFile (), file);
        ZipArchiveEntry entry = new ZipArchiveEntry (entryName);
        entry.setSize (file.length ());
        archive.putArchiveEntry (entry);
        
        BufferedInputStream input = new BufferedInputStream (
            new FileInputStream (file));
            
        IOUtils.copy (input, archive);
        input.close ();
        archive.closeArchiveEntry ();
      }
    }
    else
    {
      ZipArchiveEntry entry = new ZipArchiveEntry (source.getName ());
      entry.setSize (source.length ());
      archive.putArchiveEntry (entry);
      
      BufferedInputStream input = new BufferedInputStream (
          new FileInputStream (source));
          
      IOUtils.copy (input, archive);
      input.close ();
      archive.closeArchiveEntry ();
    }

    return this;
  }
  
  public File buildZipFile() throws IOException {
    archive.finish ();
    archiveStream.close ();
    
    LOGGER.debug ("Finished ZIP file {}", this.zip);
    
    return this.zip;
  }
  
  /**
   * Remove the leading part of each entry that contains the source
   * directory name
   *
   * @param source
   *          the directory where the file entry is found
   * @param file
   *          the file that is about to be added
   * @return the name of an archive entry
   * @throws IOException
   *           if the io fails
   */
  private static String getEntryName (File source, File file) throws IOException
  {
    int index = source.getAbsolutePath ().length () + 1;
    String path = file.getCanonicalPath ();
    
    return path.substring (index);
  }
}
