package priv;

public class Close extends Command implements Runnable {
	
	public Close(String[] cmd) {
		if(cmd.length == 1) {
			this.setCmdValid(true);
		} else {
			this.setCmdValid(false);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.isCmdValid()) {
			System.out.println("Process shutdown.");
			System.exit(-1);
		} else {
			System.out.println("Command arguments error.");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
