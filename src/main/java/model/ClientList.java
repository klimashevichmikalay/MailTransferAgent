package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import modelsListeners.ClientListView;

public class ClientList {

    private final Map<Integer, ClientListener> clientList;
    private ClientListView clv;
    private int numberOfClient;

    public void addClientListView(ClientListView clvCopy) {
        this.clv = clvCopy;
    }

    public ClientList() {
        clientList = new HashMap<>();
        numberOfClient = 0;
    }

    public void add(ClientListener cl) {
        if (!(cl.isClosed())) {
            clientList.put(numberOfClient++, cl);
            notifyClientsList();
        }
    }

    public void remove(int i) {
        clientList.get(i).close();
        clientList.remove(i);
        notifyClientsList();
    }

    public void removeAll() {
        Iterator<Map.Entry<Integer, ClientListener>> itr = clientList.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Integer, ClientListener> clLstr = itr.next();
            clLstr.getValue().close();
        }
        clientList.clear();
    }

    private void notifyClientsList() {
        clv.update(this);
    }
}