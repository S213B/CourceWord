package readerWriter;

public class ReaderWriter {
	/*
	 * There are 5 readers and writes respectively.
	 */
	private static final int num = 5;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Operator[] writer, reader;
		SharedData data;
		
		data = new SharedData();
		writer = new Operator[5];
		reader = new Operator[5];
		
		for(int i = 0; i < num; i++) {
			reader[i] = new Operator("Reader_#" + i, data);
			writer[i] = new Operator("Writer_#" + i, data);
		}
		for(int i = 0; i < num; i++) {
			reader[i].start();
			writer[i].start();
		}
		
	}

}
