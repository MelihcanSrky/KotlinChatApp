const pool = require('../../db/db');

const getFriends = async (req, res, next) => {
    try {
        const { user_uuid } = req.query;
        const friends = await pool.query(
            "SELECT uuid, username, firstname, lastname FROM users WHERE uuid IN (SELECT friend_uuid FROM friends WHERE user_uuid = $1)",
            [user_uuid]
        );

        res.status(200).json({
            status: 200,
            message: 'Friends fetched successfully',
            data: friends.rows
        })
    } catch (error) {
        next(error);
    }
}

module.exports = {
    getFriends
}