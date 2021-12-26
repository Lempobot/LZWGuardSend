package LZW;

/**
 * LZW PASSWORD PROTECTED PROJECT
 * Submitted by:
 * Student 1: 	DANNY KOGEL 318503257
 * Student 2. 	ALEX BREGER 205580087
 */

import GUI.LZWGuardGUI;
import LZW.Compressor.LZW_Compressor_Decompressor;
import LZW.Guard.GuardEncrypt;
import LZW.Guard.GuardExecption;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import static GUI.LZWGuardGUI.loading;

public class Runner {

	//queue that stores paths for dropped files
	public static Queue<String> inputSourceQueue = new LinkedList<>();
	//queue that stores names for dropped files
	public static Queue<String> FileQueue = new LinkedList<>();

	//setters
	public static void setActivateGuard(boolean activateGuard) {
		Runner.activateGuard = activateGuard;
	}
	public static void setPassword(String password) {
		Runner.password = padPassword(password);
	}

	//boolean check to see if we should encrypt files
	static boolean activateGuard = false;

	//default password
	static String password = padPassword("1234567891011121");

	static LZW_Compressor_Decompressor LZWmainClassCompressDecompress = new LZW_Compressor_Decompressor();

	//input out path paths for reading and special paths for encrypted files
	static String[] IN_FILE_PATH = new String[]{"",""};
	static String[] OUT_FILE_PATH = new String[]{""};
	static String[] ENCRYPTED_FILE_PATH = new String[]{""};

	//runs the entire compress sequence, creates Compressedfiles folder if non exist, reads from queue all
	//files and compresses them one by one, if activateGuard enabled, will also encrypt files in the end.
	public static void RunCompress() throws GuardExecption {
		LZWGuardGUI.itemCounter = 0;
		File theDir = new File("..\\CompressedFiles");
		if (!theDir.exists()){
			theDir.mkdirs();
		}
		while(!FileQueue.isEmpty()){
			String FileName = FileQueue.remove();
			if(!FileName.contains(".lzw")) {
				IN_FILE_PATH[0] = inputSourceQueue.remove();
				IN_FILE_PATH[1] = "..\\CompressedFiles\\" + FileName + ".lzw";
				OUT_FILE_PATH[0] = "..\\DecompressedFiles\\Decompressed" + FileName;
				if (activateGuard) {
					CompressGuarded();
				} else
					Compress();
				loading.setText("Compressing is done!");
			}else {
				//will skip any compressed files.
				LZWGuardGUI.loading.setText("Some of the files are already compressed...");
				}
		}
	}
	//runs the entire Decompress sequence, creates Deompressedfiles folder if non exist, reads from queue all
	//files and Decompresses them one by one, if activateGuard enabled, will also decrypt files in the end.
	public static void RunDecompress() throws GuardExecption {

		LZWGuardGUI.itemCounter = 0;

		File theDir = new File("..\\DecompressedFiles");
		if (!theDir.exists()){
			theDir.mkdirs();
		}

		while(!FileQueue.isEmpty()){
			String FileName = FileQueue.remove();
			if (FileName.contains(".lzw")) {
				IN_FILE_PATH[1] = inputSourceQueue.remove();

				FileName = FileName.replace(".lzw","");
				FileName = FileName.replace(".guard","");
				OUT_FILE_PATH[0] = "..\\DecompressedFiles\\" + FileName;
				if(activateGuard){
					DecryptGuarded();
				}
				if(IN_FILE_PATH[1].contains(".guard")){
					loading.setText("Some of the files are encrypted...");
					return;
				}
				Decompress();
				loading.setText("Decompressing is done!");
			}else {
				//will skip any non compressed files.
				loading.setText("Some of the files are not compressed...");
			}
		}
	}

	//pads password to 16 chars length or else AES cannot use it as a key
	public static String padPassword(String io_string_password) {
		while (io_string_password.length() < 16) {
			io_string_password = "*" + io_string_password;
		}
		return io_string_password;
	}

	//basic compress
	public static void Compress()
	{
		LZWmainClassCompressDecompress.Compress(IN_FILE_PATH, OUT_FILE_PATH);
	}

	//basic Decompress
	public static void Decompress()
	{
		LZWmainClassCompressDecompress.Decompress(IN_FILE_PATH, OUT_FILE_PATH);
	}

	//send file for compress first and then encrypts them
	public static void CompressGuarded() throws GuardExecption {
		LZWmainClassCompressDecompress.Compress(IN_FILE_PATH, OUT_FILE_PATH);
		File inputFile = new File((IN_FILE_PATH[1]));
		ENCRYPTED_FILE_PATH[0] = "..\\CompressedFiles\\" + inputFile.getName() + ".guard";
		File encryptedFile = new File(ENCRYPTED_FILE_PATH[0]);
		GuardEncrypt.encrypt(password, inputFile, encryptedFile);
		inputFile.delete();
	}

	//decrypts file
	public static void DecryptGuarded(){
		try {
			File encryptedFile = new File(IN_FILE_PATH[1]);
			File decryptedFile = new File((IN_FILE_PATH[1].replace(".guard", "")));
			GuardEncrypt.decrypt(password, encryptedFile, decryptedFile);
			IN_FILE_PATH[1] = decryptedFile.getPath();

		}catch (GuardExecption e){ }
	}
}
