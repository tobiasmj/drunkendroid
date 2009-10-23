package drunkendroidserver.Command;

import java.util.LinkedList;


public class Transaction {
	private LinkedList<ICommand> commandList;
	public Transaction(){
		commandList = new LinkedList<ICommand>();
	}
	public void Commit(){
		for(ICommand com : commandList) {
			com.Execute();
		}
	}
	public void addCommand(ICommand command) {
		commandList.add(command);
	}
	
}
