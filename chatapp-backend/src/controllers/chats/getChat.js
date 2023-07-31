const pool = require('../../db/db');

const getChat = async (req, res, next) => {
    try {
        const { user_uuid } = req.query;

        const chat = await pool.query(
            `SELECT chats.*, last_messages.message AS last_message, last_messages.send_at AS last_message_at
  FROM chats
  LEFT JOIN (
    SELECT DISTINCT ON (chat_uuid) chat_uuid, message, send_at
    FROM messages
    ORDER BY chat_uuid, send_at DESC
  ) AS last_messages
  ON chats.chat_uuid = last_messages.chat_uuid
  WHERE chats.user_uuid = $1`,
            [user_uuid]
        );

        res.status(200).json({
            status: 200,
            message: 'Chat fetched successfully',
            data: chat.rows
        })
    }
    catch (error) {
        next(error);
    }
}

module.exports = {
    getChat
}