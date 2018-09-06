/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

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
