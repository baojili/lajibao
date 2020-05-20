package com.ln.base.network;


import com.ln.base.model.JsonInterface;

import java.util.List;

/**
 *
 */

public interface PageRspInterface<D> extends JsonInterface {
    List<D> getList();
}
