The jar file is an exectuable, some bugs may be present
you must unzip the jar file and run it as a jave program for it to 
properlly work

Please noitce, although the program will write "x is done!", it only means the action has finished,
if any noitces were brought up the compression or decompression probaly didn't take action

Things that may seem like a bug but suprisngly work as planned:

- Compression Done and Decompression done when nothing actually happend - this is 
a general statement that just means the end of the process, if nothing happend it will usaully pop an alert.

- Decompressing a non passworded file with the Guard option on decompresses it as normal - this seems like something that should happen,
you can open an unlocked door with a key.

- Both files of Guard and not guard version are produced during compression, although it's not good for an end product, it helps debugging.
you do not need the LZW file to decompress the Guarded one though.


Changelog:

version 0.0.74

fixed compression without the original file:

at Runner.java:135 

from
	public static void DecryptGuarded(){
		try {
			File inputFile = new File((IN_FILE_PATH[1]));
			ENCRYPTED_FILE_PATH[0] = "..\\CompressedFiles\\" + inputFile.getName();
			File encryptedFile = new File(ENCRYPTED_FILE_PATH[0]);
			File decryptedFile = new File((OUT_FILE_PATH[0]));
			GuardEncrypt.decrypt(password, encryptedFile, decryptedFile);
			IN_FILE_PATH[1] = "..\\CompressedFiles\\" + inputFile.getName().replace(".guard", "");

		}catch (GuardExecption e){ }
	}

to


	public static void DecryptGuarded(){
		try {
			File encryptedFile = new File(IN_FILE_PATH[1]);
			File decryptedFile = new File((IN_FILE_PATH[1].replace(".guard", "")));
			GuardEncrypt.decrypt(password, encryptedFile, decryptedFile);
			IN_FILE_PATH[1] = decryptedFile.getPath();

		}catch (GuardExecption e){ }
	}

seems like the function wasnt changed to the working state before.

also:

GuardEncrypt:65
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

two catches taken out of the main catch, those probelms seem to be not breaking bugs,
 so no user changes are needed thus no warning.
 
 

version 0.0.71 

added status bar

version 0.0.74

added folders menu for ease of use

version 0.0.81(current)

no longer throw random warnings