const pool = require('../../db/db');

const deleteFriend = async (req, res, next) => {
    try {
        const { user_uuid, friend_uuid } = req.body;
        const client = await pool.connect();
        try {
            await client.query("BEGIN");
            let deleteFriend = await client.query(
                "DELETE FROM friends WHERE user_uuid = $1 AND friend_uuid = $2 RETURNING *",
                [user_uuid, friend_uuid]
            );
            if (deleteFriend.rowCount === 0) {
                return res.status(404).json({
                    status: 404,
                    message: 'Friend not found',
                    data: null
                })
            }

            deleteFriend = await client.query(
                "DELETE FROM friends WHERE user_uuid = $1 AND friend_uuid = $2 RETURNING *",
                [friend_uuid, user_uuid]
            );
            if (deleteFriend.rowCount === 0) {
                return res.status(404).json({
                    status: 404,
                    message: 'Friend not found',
                    data: null
                })
            }

            await client.query("COMMIT");

            res.status(200).json({
                status: 200,
                message: 'Friend deleted successfully',
                data: null
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
    deleteFriend
}