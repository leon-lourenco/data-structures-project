package com.datastructures.linear.linkedlist.applied;

import com.datastructures.linear.linkedlist.classic.LinkedList;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An insurance claim's processing pipeline (insurer): a fixed default sequence of stages
 * (intake, document verification, assessment, payout) that individual claims can have *extra*
 * stages spliced into mid-pipeline — a high-value claim might need a "manual review" stage
 * inserted right after document verification, without touching intake or assessment.
 *
 * <p>That insertion is exactly what a linked list is for here: with an array-backed list,
 * inserting a stage in the middle means shifting every stage after it — O(n). Here it's
 * splicing one node in — O(1), regardless of how many stages come after.
 */
public final class ClaimWorkflow {

    private final LinkedList<ClaimStage> stages = new LinkedList<>();

    public ClaimWorkflow() {
        stages.addLast(new ClaimStage("intake"));
        stages.addLast(new ClaimStage("document-verification"));
        stages.addLast(new ClaimStage("assessment"));
        stages.addLast(new ClaimStage("payout"));
    }

    public List<String> stageNames() {
        List<String> names = new ArrayList<>();
        for (ClaimStage stage : stages) {
            names.add(stage.name());
        }
        return names;
    }

    /** Splices a new stage in right after {@code afterStageName} — O(1), no reshuffling. */
    public void insertStageAfter(String afterStageName, String newStageName) {
        LinkedList.Node<ClaimStage> anchor = findNode(afterStageName);
        stages.insertAfter(anchor, new ClaimStage(newStageName));
    }

    private LinkedList.Node<ClaimStage> findNode(String stageName) {
        LinkedList.Node<ClaimStage> current = stages.headNode();
        while (current != null) {
            if (current.value().name().equals(stageName)) {
                return current;
            }
            current = current.next();
        }
        throw new NoSuchElementException("no stage named " + stageName);
    }
}
