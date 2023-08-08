package sgs.env.ecabsdriver.interfce;

public interface StatusCall {
    void arrivedSuccess();
    void arrivedFailure();

    void cancelSuccess();
    void cancelFailure();
}
