CREATE TABLE "user" (
  id           TEXT PRIMARY KEY,
  display_name TEXT UNIQUE,
  avatar_url   TEXT,
  password     TEXT NOT NULL,
  kind         TEXT DEFAULT 'user'
);

CREATE TABLE "device" (
  device_id    TEXT UNIQUE NOT NULL,
  user_id      TEXT        NOT NULL,
  token        TEXT UNIQUE,
  display_name TEXT,
  last_seen_ip TEXT,
  last_seen_ts BIGINT,
  PRIMARY KEY (device_id, user_id),
  FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE "user_interactive_session" (
  session_id TEXT PRIMARY KEY,
  completed  TEXT [],
  created    TIMESTAMP WITH TIME ZONE NOT NULL,
  updated    TIMESTAMP WITH TIME ZONE NOT NULL
);
