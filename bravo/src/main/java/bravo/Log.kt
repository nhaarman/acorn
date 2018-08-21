package bravo

import com.nhaarman.bravo.Logger

/**
 * The logger instance Bravo uses to log statements, an alias for [com.nhaarman.bravo.logger].
 *
 * @see com.nhaarman.bravo.logger
 */
var logger: Logger?
    inline get() = com.nhaarman.bravo.logger
    inline set(value) {
        com.nhaarman.bravo.logger = value
    }
