const pool = require('../../db/db');
const { v4: uuidv4 } = require('uuid');

const createChat = async (req, res, next) => {
	try {
		const { user_uuid, friend_uuid } = req.body;
		const chat_uuid = uuidv4();
		const create_at = BigInt(new Date().getTime());
		const client = await pool.connect();
		try {
			await client.query("BEGIN");
			const me = await client.query(
				"SELECT * FROM users WHERE uuid = $1",
				[user_uuid]
			);
			if (me.rowCount === 0) {
				return res.status(404).json({
					status: 404,
					message: 'User not found',
					data: null
				})
			}

			const friend = await client.query(
				"SELECT * FROM users WHERE uuid = $1",
				[friend_uuid]
			);
			if (friend.rowCount === 0) {
				return res.status(404).json({
					status: 404,
					message: 'Friend not found',
					data: null
				})
			}

			const chatExists = await client.query(
				"SELECT COUNT(*) AS count FROM chats WHERE (user_uuid = $1 AND chatname = $4) OR (user_uuid = $2 AND chatname = $3)",
				[user_uuid, friend_uuid, me.rows[0].username, friend.rows[0].username]
			);
			if (chatExists.rows[0].count > 0) {
				const chat = await client.query(
					"SELECT * FROM chats WHERE user_uuid = $1 and chatname = $2",
					[user_uuid, friend.rows[0].username]
				)
				return res.status(200).json({
					status: 200,
					message: 'chat already exists',
					data: {
						chat_uuid: chat.rows[0].chat_uuid,
						chatname: friend.rows[0].username
					}
				})
			}

			const user = await client.query(
				"INSERT INTO chats (chat_uuid, user_uuid, chatname, created_at) VALUES ($1, $2, $3, $4) RETURNING *",
				[chat_uuid, user_uuid, friend.rows[0].username, create_at]
			);

			const user2 = await client.query(
				"INSERT INTO chats (chat_uuid, user_uuid, chatname, created_at) VALUES ($1, $2, $3, $4) RETURNING *",
				[chat_uuid, friend_uuid, me.rows[0].username, create_at]
			);

			await client.query("COMMIT");

			res.status(201).json({
				status: 201,
				message: 'Chat created successfully',
				data: {
					chat_uuid,
					chatname: me.rows[0].username
				}
			})
		} catch (error) {
			await client.query("ROLLBACK");
			next(error);
		} finally {
			client.release();
		}
	} catch (error) {
		next(error);
	}
}

module.exports = {
	createChat
}
