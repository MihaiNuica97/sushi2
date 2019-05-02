package comp1206.sushi.common;

import java.io.Serializable;

public class Message implements Serializable
{
    private Object object;
    private String instructions;
    private User user;
    
    //for Server -> Client comms
    public Message(Object object, String instruction)
    {
        this.instructions = instruction;
        this.object = object;
        this.user = null;
    }
    //for Client -> Server comms
    public Message(Object object, String instruction, User user)
    {
        this.instructions = instruction;
        this.object = object;
        this.user = user;
    }
    
    
    public Object getObject()
    {
        return object;
    }
    
    public String getInstructions()
    {
        return instructions;
    }
    
    public User getUser()
    {
        return user;
    }
}
