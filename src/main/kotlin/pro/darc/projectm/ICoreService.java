package pro.darc.projectm;

// provide lifecycle for the services
public interface ICoreService {

    void OnLoaded();
    void OnStarted();
    void OnStopped();

}
