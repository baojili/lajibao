package com.ln.base.model;

/**
 * 地级县类
 *
 * @author
 */
public class DistrictModel extends JsonEntity {
    /**
     * 地级县名称
     */
    private String name;

    public DistrictModel() {
        super();
    }

    public DistrictModel(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DistrictModel [name=" + name + "]";
    }

}
