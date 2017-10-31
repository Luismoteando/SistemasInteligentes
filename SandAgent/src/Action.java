import java.util.*;

//Leave sand
public class Action {
	private int northSand, westSand, eastSand, southSand;
	private Movement moves;
	private static List<Action> actions = new ArrayList<Action>();


	public Action(int northSand, int westSand, int eastSand, int southSand) {
		this.northSand = northSand;
		this.westSand = westSand;
		this.eastSand = eastSand;
		this.southSand = southSand;
	}

	public Action(Movement moves, List<Action> action) {
		this.moves = moves;
		Action.actions = action;
	}

	public static void generateActions(Tractor t, Field f, Movement m){
		int[] elementos = possibilities(t, f);
		int n = 4;                  //Tipos para escoger
		int r = elementos.length;   //Elementos elegidos

		recursiveActions(elementos, "", n, r, f, t, m);
	}//end generateActions

	public static void recursiveActions(int[] elem, String act, int n, int r, Field f, Tractor t, Movement m) {
		int integer;
		int[] next = null;
		Action na;
		if (n == 0) {
			integer = Integer.parseInt(act);
			next = splitNumbers(integer);
			if(validActions(next, f, t, m)){
				na = new Action(next[0], next[1], next[2], next[3]);
				actions.add(na);
			}
		} else {
			for (int i = 0; i < r; i++) {
				recursiveActions(elem, act + elem[i], n - 1, r, f, t, m);
			}
		}
	}//end Perm1

	public static int[] possibilities(Tractor t, Field f){
		int difference;
		difference = f.getDifference(t);

		if(f.getField()[t.getX()][t.getY()] < f.getK()){
			System.out.println("Trying to move sand from a box that is lesser than 'k' (The desired quantity)");
			System.exit(0);
		}
		int [] possibilities = new int[difference + 1];
		for(int i = 0; i < possibilities.length; i++){
			possibilities[i] = difference - i;
		}
		return possibilities;
	}
	public static int[] splitNumbers(int integer){
		int[] comb = new int[4];

		comb[3] = integer%10;

		integer = integer/10;
		comb[2] = integer%10;

		integer = integer/10;
		comb[1] = integer%10;

		integer = integer/10;
		comb[0] = integer%10;

		return comb;
	}

	public static boolean validActions(int[] next, Field f, Tractor t, Movement m){
		int difference = f.getDifference(t);		

		if((next[0] + next[1] + next[2] + next[3]) != difference)
			return false;
		//metodo para comprobar el maximo (cogemos los metodos get de los movimientos y vemos si su valor supera el maximo)
		//if()
		if(!f.checkSuccessors(next, t, m))
			return false;

		return true;		
	}
	
	public static  List<Action> actionsWithMovements(Movement m, Tractor t, Field f){
		List<Action> actionsWithMoves = new ArrayList<Action>();
		Action action = null;
		Movement mv = null;
		System.out.println("\nThe list containing the valid actions is:");

		if(t.getX() != 0){
			actionsWithMoves.add(actionsForEachMovement(mv, m.getNorthMovement(t)[0], m.getNorthMovement(t)[1], action, actionsWithMoves));

		}if(t.getY() != 0){
			actionsWithMoves.add(actionsForEachMovement(mv, m.getWestMovement(t)[0], m.getWestMovement(t)[1], action, actionsWithMoves));

		}if(t.getY() != f.getColumn() - 1){
			actionsWithMoves.add(actionsForEachMovement(mv, m.getEastMovement(t, f)[0], m.getEastMovement(t, f)[1], action, actionsWithMoves));

		}if(t.getX() != f.getRow() - 1){
			actionsWithMoves.add(actionsForEachMovement(mv, m.getSouthMovement(t, f)[0], m.getSouthMovement(t, f)[1], action, actionsWithMoves));
		}		
		printActions(actionsWithMoves);
		return actionsWithMoves;		
	}
	
	public static Action actionsForEachMovement(Movement mv, int hor, int ver, Action action, List<Action> actionsWithMoves){
		int[] moves = new int[2];
		moves[0] = hor;
		moves[1] = ver;
		mv = new Movement(moves);
		action = new Action(mv, actions);		
		return action;		
	}

	public static List<Field> tryActions(Movement m, List<Action> a, Field f, Tractor t){
		Action auxAction, act;
		Movement mv;
		List<Field> fieldList= new ArrayList<Field>();
		Field auxField;
		for(int i = 0; i < a.size(); i++){
			auxAction = a.get(i);
			mv = auxAction.getMoves();
			for(int j = 0; j < auxAction.getActions().size(); j++){
				int[][] possible = createPossibleField(f);
				possible[t.getX()][t.getY()] = possible[t.getX()][t.getY()] - f.getDifference(t);
				act = auxAction.getActions().get(j);
				if(act.getNorthSand() != 0)
					possible[m.getNorthMovement(t)[0]][m.getNorthMovement(t)[1]] = possible[m.getNorthMovement(t)[0]][m.getNorthMovement(t)[1]] + act.getNorthSand();		

				if(act.getWestSand() != 0)
					possible[m.getWestMovement(t)[0]][m.getWestMovement(t)[1]] = possible[m.getWestMovement(t)[0]][m.getWestMovement(t)[1]] + act.getWestSand();	

				if(act.getEastSand() != 0)
					possible[m.getEastMovement(t, f)[0]][m.getEastMovement(t, f)[1]] = possible[m.getEastMovement(t, f)[0]][m.getEastMovement(t, f)[1]] + act.getEastSand();

				if(act.getSouthSand() != 0)
					possible[m.getSouthMovement(t, f)[0]][m.getSouthMovement(t, f)[1]] = possible[m.getSouthMovement(t, f)[0]][m.getSouthMovement(t, f)[1]] + act.getSouthSand();
				
				auxField = new Field(mv.getHorizontal(), mv.getVertical(), possible, f.getK(), f.getMax());
				fieldList.add(auxField);

				System.out.println(mv.toString());
				for(int x = 0; x < possible.length; x++){
					for(int y = 0; y < possible[x].length; y++){
						System.out.print("|" + possible[x][y]);
					}
					System.out.print("|\n");
				}
			}
		}//end for
		System.out.print("\n");
		
		return fieldList;		
	}
	
	public List<Node> successors(Node parent, Movement m, Tractor t, Field f){
		List<Action> actionList = actionsWithMovements(m, t, f);
		List<Node> successors = new ArrayList<Node>();
		List<Field> fieldList = tryActions(m, actionList, f, t);
		Action auxAction;
		Node node;
		Field auxField;
		for(int i = 0; i < actionList.size(); i++){
			auxAction = actionList.get(i);
			auxField = fieldList.get(i);
			node = new Node(parent, auxField, parent.getCost() + 1, parent.getDepth() + 1, auxAction);
			successors.add(node);			
		}
		
		return successors;	
	}

	public static int[][] createPossibleField(Field f){
		int[][] possible = new int[f.getField().length][f.getField().length];
		for(int x = 0; x < f.getField().length; x++){
			for(int y = 0; y < f.getField()[x].length; y++){
				possible[x][y] = f.getField()[x][y];
			}
		}
		return possible;
	}

	public int getNorthSand() {
		return northSand;
	}

	public void setNorthSand(int northSand) {
		this.northSand = northSand;
	}

	public int getWestSand() {
		return westSand;
	}

	public void setWestSand(int westSand) {
		this.westSand = westSand;
	}

	public int getEastSand() {
		return eastSand;
	}

	public void setEastSand(int eastSand) {
		this.eastSand = eastSand;
	}

	public int getSouthSand() {
		return southSand;
	}

	public void setSouthSand(int southSand) {
		this.southSand = southSand;
	}

	public Movement getMoves() {
		return moves;
	}

	public void setMoves(Movement moves) {
		this.moves = moves;
	}

	public List<Action> getActions() {
		return actions;
	}

	public static void setActions(List<Action> actions) {
		Action.actions = actions;
	}

	public static void printActions(List<Action> actionsWithMoves){
		for(int i = 0; i < actionsWithMoves.size(); i++)
			System.out.println(actionsWithMoves.get(i).printWithMoves() + "\n");
	}

	@Override
	public String toString() {
		return "\nAction [northSand=" + northSand + ", westSand=" + westSand + ", eastSand=" + eastSand + ", southSand="
				+ southSand + "]";
	}
	public String printWithMoves(){
		return moves.toString() + actions.toString();
	}


}
