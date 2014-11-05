package com.eulersbridge.iEngage.core.services;

import com.eulersbridge.iEngage.core.events.likes.LikeableObjectLikesEvent;
import com.eulersbridge.iEngage.core.events.likes.LikesLikeableObjectEvent;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by darcular on 5/11/14.
 */
public interface LikesService {
    @PreAuthorize("hasRole('ROLE_USER')")
    public LikeableObjectLikesEvent likes(LikesLikeableObjectEvent likesLikeableObjectEvent, Sort.Direction sortDirection, int pageNumber, int pageSize);
}
