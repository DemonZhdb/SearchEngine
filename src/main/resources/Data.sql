INSERT  INTO field (name, selector, weight)
VALUES ('title', 'title', 1.0),('body', 'body', 0.8) ON CONFLICT DO NOTHING ;