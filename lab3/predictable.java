import java.util.ArrayList;

//just used for seralizing so compiler knows that the object can be predicted
public interface predictable {
    public observation predict(observation example);
}
