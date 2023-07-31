const { CustomError } = require("../error/errorHandler")
const jwt = require('jsonwebtoken')

function verifyToken(req, res, next) {
    const token = req.get('Authorization')

    if (!token) {
        return next(
            new CustomError('Token not found', 403)
        )
    }

    jwt.verify(token, process.env.JWT_SECRET, (err, decoded) => {
        if (err) {
            return next(
                new CustomError('Token is invalid', 401)
            )
        }

        req.user = decoded
        next()
    })
}

module.exports = {
    verifyToken
}