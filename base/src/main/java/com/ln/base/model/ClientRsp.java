package com.ln.base.model;

import com.ln.base.network.BaseRsp;

public class ClientRsp extends BaseRsp {
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
