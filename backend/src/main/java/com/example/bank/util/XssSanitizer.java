package com.example.bank.util;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
public final class XssSanitizer {

    /** A very strict policy: no tags allowed, plain text only. */
    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    private XssSanitizer() {}

    /**
     * Strip every HTML tag and dangerous attribute from {@code input}.
     * Returns {@code null} if the input is {@code null}.
     */
    public static String clean(String input) {
        if (input == null) {
            return null;
        }
        // Apply OWASP sanitizer first to remove dangerous constructs,
        // then strip any remaining tags so we end up with pure text.
        String sanitized = POLICY.sanitize(input);
        // Strip remaining HTML tags (the policy allows some formatting tags,
        // but for a product name we want plain text).
        return sanitized.replaceAll("<[^>]+>", "").trim();
    }
}