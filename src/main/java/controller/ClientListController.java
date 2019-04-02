package controller;

import model.ClientList;
import model.ClientListener;

public class ClientListController {

    public void execute(ClientList cList, ClientListControllerOperations op, ClientListener cListner, int cLNumber) {
        switch (op) {
            case ADD: {
                cList.add(cListner);
                break;
            }
            case REMOVE: {
                cList.remove(cLNumber);
                break;
            }
            case CLOSE_ALL: {
                cList.removeAll();
                break;
            }
        }
    }
}