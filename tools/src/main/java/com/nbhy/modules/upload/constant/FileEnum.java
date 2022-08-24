package com.nbhy.modules.upload.constant;

public enum  FileEnum {
    IMAGE("image"),
    VIDEO("video"),
    DOCUMENT("document"),
    AUDIO("audio");


    private  String type;

    FileEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }


    /**
     * 提前判断，用于解决
     * Case中出现的Constant expression required
     * @param type
     * @return
     */
    public static FileEnum getByValue(String type){
        for(FileEnum fileEnum:values()){
            if(fileEnum.getType()==type){
                return fileEnum;
            }
        }
        return null;
    }

}
