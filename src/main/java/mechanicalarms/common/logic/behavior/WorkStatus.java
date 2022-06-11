package mechanicalarms.common.logic.behavior;

public class WorkStatus {
    private ActionTypes type;
    private Action action;

    public WorkStatus() {
        this.type = ActionTypes.IDLING;
        this.action = Action.IDLING;
    }

    public void idle() {
        this.type = ActionTypes.IDLING;
        this.action = Action.IDLING;
    }

    public ActionTypes getType() {
        return type;
    }

    public Action getAction() {
        return action;
    }

    public Action setAction(Action value) {
        this.action = value;
        return value;
    }

    public ActionTypes setType(ActionTypes value) {
        this.type = value;
        return type;
    }
}
