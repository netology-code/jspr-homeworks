package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {
    private final ConcurrentHashMap<Long, Post> map = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public List<Post> all() {
        return new ArrayList<>(map.values());
    }

    public Optional<Post> getById(long id) {
        var post = map.get(id);
        if (post != null) return Optional.of(post);
        else return Optional.empty();
    }

    public Post save(Post post) {
        long id = post.getId();
        if (id == 0) {
            long postId = counter.getAndIncrement();
            post.setId(postId);
            map.put(postId, post);
            return post;
        }
        if (map.containsKey(id)) {
            map.put(id, post);
            return post;
        }
        throw new NotFoundException();
    }

    public void removeById(long id) {
        map.remove(id);
    }
}
