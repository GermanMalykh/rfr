DELETE pl
FROM photo_like pl
         INNER JOIN `like` l ON pl.like_id = l.id
         INNER JOIN `user` u ON l.user_id = u.id
WHERE u.username = :username
