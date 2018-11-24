/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package acorn

import com.nhaarman.acorn.Logger

/**
 * The logger instance Acorn uses to log statements, an alias for [com.nhaarman.acorn.logger].
 *
 * @see com.nhaarman.acorn.logger
 */
var logger: Logger?
    inline get() = com.nhaarman.acorn.logger
    inline set(value) {
        com.nhaarman.acorn.logger = value
    }
