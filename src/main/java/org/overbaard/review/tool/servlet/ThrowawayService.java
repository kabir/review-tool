package org.overbaard.review.tool.servlet;

import javax.enterprise.context.RequestScoped;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@RequestScoped
public class ThrowawayService {

    public int getValue() {
        System.out.println("Returning 99");
        return 99;
    }
}
