/**
 * Thrown internally when the drone hits the per-run tick budget or hard step cap.
 * The Runner catches this as a CLEAN STOP (not a player error): a continuous
 * {@code while (true)} program is expected to end this way. See AGENTS.md section 6.
 */
public class BudgetReachedException extends RuntimeException {
    public BudgetReachedException(String message) { super(message); }
}
