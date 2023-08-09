const express = require('express');
const { createUser } = require('../controllers/users/createUser');
const { loginUser } = require('../controllers/users/loginUser');
const { verifyToken } = require('../middlewares/jwt/verify_token');
const { sendRequest } = require('../controllers/friends/sendRequest');
const { acceptOrDecline } = require('../controllers/friends/accept_or_decline');
const { getUsers } = require('../controllers/friends/getUsers');
const { getRequests } = require('../controllers/friends/getRequests');
const { getFriends } = require('../controllers/friends/getFriends');
const { getChat } = require('../controllers/chats/getChat');
const { createChat } = require('../controllers/chats/createChat');
const { getMessages } = require('../controllers/messages/getMessages');
const { sendMessage } = require('../controllers/messages/sendMessage');
const { deleteFriend } = require('../controllers/friends/deleteFriend');
const router = express.Router();

router.post("/users/create", createUser);
router.post("/users/login", loginUser);
router.get("/users/:user_uuid", verifyToken, getUsers);

router.get("/requests", verifyToken, getRequests);

router.get("/friends", verifyToken, getFriends);
router.post("/friends/send", verifyToken, sendRequest);
router.put("/friends/accept", verifyToken, acceptOrDecline);
router.delete("/friends/delete", verifyToken, deleteFriend);

router.get("/chats", verifyToken, getChat);
router.post("/chats/create", verifyToken, createChat);

router.get("/chat/:chat_uuid/messages", verifyToken, getMessages)
router.post("/chat/:chat_uuid/messages/send", verifyToken, sendMessage);

module.exports = router;