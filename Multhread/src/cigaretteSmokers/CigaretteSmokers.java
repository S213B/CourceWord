package cigaretteSmokers;

public class CigaretteSmokers {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Smoker[] needPaper, needMatch, needTobacco;
		Ingredient paper, match, tobacco;
		
		needPaper = new Smoker[10];
		needMatch = new Smoker[10];
		needTobacco = new Smoker[10];
		paper = new Ingredient("paper");
		match = new Ingredient("match");
		tobacco = new Ingredient("tobacco");
		
		int j = (int) (Math.random()*9 + 2);
		for(int i = 0; i < j; i++) {
			needPaper[i] = new Smoker("ManNeedPaper_" + i, paper);
			needMatch[i] = new Smoker("ManNeedMatch_" + i, match);
			needTobacco[i] = new Smoker("ManNeedTobacco_" + i, tobacco);
		}
		
		System.out.println("There are " + j*3 + " smokers.\n" + "Each kind has " + j + " smokers.");
		
		for(int i = 0; i < j; i++) {
			needPaper[i].start();
			needMatch[i].start();
			needTobacco[i].start();
		}
	}

}
