package com.beaglebuddy.mpeg.pojo;

/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <a target="_blank" href="http://lame.sourceforge.net/">LAME</a> header version information.
 * @see com.beaglebuddy.mpeg.LAMEHeader
 * @see <a href="http://wiki.hydrogenaud.io/index.php?title=LAME_version_string" target="_blank">LAME version string</a>
 * @see <a href="http://gabriel.mp3-tech.org/mp3infotag.html"                    target="_blank">LAME Header Format</a>
 */
public class Version
{
   // data members
   private int  major;
   private int  minor;
   private char flag;     // a = alpha, b = beta, r = release


   /**
    * constructor.
    * @param major  the major version.
    * @param minor  the minor version.
    * @param flag   indicates the release type of the version.
    */
   public Version(int major, int minor, char flag)
   {
      this.major = major;
      this.minor = minor;
      this.flag  = flag;
   }

   /**
    * get the major <a target="_blank" href="http://lame.sourceforge.net/">LAME</a> version.
    * @return the major LAME version.
    */
   public int getMajor()
   {
      return major;
   }

   /**
    * get the minor <a target="_blank" href="http://lame.sourceforge.net/">LAME</a> version.
    * @return the minor LAME version.
    */
   public int getMinor()
   {
      return minor;
   }

   /**
    * gets the release type for the version.
    * @return the <a target="_blank" href="http://lame.sourceforge.net/">LAME</a> version flag, where:
    * <table>
    *    <tr><td>a     </td><td>alpha                            </td></tr>
    *    <tr><td>b     </td><td>beta                             </td></tr>
    *    <tr><td>r     </td><td>release with patch version &gt; 0</td></tr>
    *    <tr><td>&nbsp;</td><td>release with no patch version    </td></tr>
    * </table>
    */
   public char getFlag()
   {
      return flag;
   }

   /**
    * gets a a string representation of the <a target="_blank" href="http://lame.sourceforge.net/">LAME</a> header version.
    * @return a string representation of the Lame header version.
    */
   @Override
   public String toString()
   {
      return "" + major + "." + minor + (flag == ' ' ? flag : "");
   }
}
