const pool = require('../../db/db');

const getRequests = async (req, res, next) => {
    try {
        const { user_uuid } = req.query;
        const requests = await pool.query(
            "SELECT uuid, username, firstname, lastname FROM users WHERE uuid IN (SELECT sender_uuid FROM requests WHERE user_uuid = $1 ORDER BY send_at DESC)",
            [user_uuid]
        );

        res.status(200).json({
            status: 200,
            message: 'Requests fetched successfully',
            data: requests.rows
        })
    } catch (error) {
        next(error);
    }
}

module.exports = {
    getRequests
}