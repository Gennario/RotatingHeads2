package cz.gennario.newrotatingheads.developer;

import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.rotatingengine.actions.ActionResponse;

public class ActionDevTools {

    private final Main instance;

    public ActionDevTools() {
        this.instance = Main.getInstance();
    }

    /**
     * > This function creates an action with the given identifier and action response
     *
     * @param identifier The identifier of the action.
     * @param actionResponse This is the response that will be sent to the user when the action is triggered.
     */
    public void createAction(String identifier, ActionResponse actionResponse) {
        instance.getActionsAPI().addAction(identifier, actionResponse);
    }

}
