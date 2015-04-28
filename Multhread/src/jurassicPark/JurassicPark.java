package jurassicPark;

public class JurassicPark {
	public static final int peopleNum = 10, carNum = 3;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		People[] people;
		Car[] car;
		SafariArea sa;
		
		people = new People[peopleNum];
		car = new Car[carNum];
		sa = new SafariArea();
		
		for(int i = 0; i < carNum; i++) {
			car[i] = new Car("Car_#" + i, sa);
			car[i].start();
		}
		
		for(int i = 0; i < peopleNum; i++) {
			people[i] = new People("People_#" + i, sa);
			people[i].start();
		}
	}

}
