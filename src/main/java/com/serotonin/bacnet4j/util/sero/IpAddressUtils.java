/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * When signing a commercial license with Infinite Automation Software,
 * the following extension to GPL is made. A special exception to the GPL is
 * included to allow you to distribute a combined work that includes BAcnet4J
 * without being obliged to provide the source code for any proprietary components.
 *
 * See www.infiniteautomation.com for commercial license options.
 * 
 * @author Matthew Lohbihler
 */
package com.serotonin.bacnet4j.util.sero;

/**
 * @author Matthew Lohbihler
 */
public class IpAddressUtils {
    public static boolean ipWhiteListCheck(String allowedIp, String remoteIp) throws IpWhiteListException {
        String[] remoteIpParts = remoteIp.split("\\.");
        if (remoteIpParts.length != 4)
            throw new IpWhiteListException("Invalid remote IP address: " + remoteIp);
        return ipWhiteListCheckImpl(allowedIp, remoteIp, remoteIpParts);
    }

    public static boolean ipWhiteListCheck(String[] allowedIps, String remoteIp) throws IpWhiteListException {
        String[] remoteIpParts = remoteIp.split("\\.");
        if (remoteIpParts.length != 4)
            throw new IpWhiteListException("Invalid remote IP address: " + remoteIp);

        for (int i = 0; i < allowedIps.length; i++) {
            if (ipWhiteListCheckImpl(allowedIps[i], remoteIp, remoteIpParts))
                return true;
        }

        return false;
    }

    private static boolean ipWhiteListCheckImpl(String allowedIp, String remoteIp, String[] remoteIpParts)
            throws IpWhiteListException {
        String[] allowedIpParts = allowedIp.split("\\.");
        if (allowedIpParts.length != 4)
            throw new IpWhiteListException("Invalid allowed IP address: " + allowedIp);

        return validateIpPart(allowedIpParts[0], remoteIpParts[0], allowedIp, remoteIp)
                && validateIpPart(allowedIpParts[1], remoteIpParts[1], allowedIp, remoteIp)
                && validateIpPart(allowedIpParts[2], remoteIpParts[2], allowedIp, remoteIp)
                && validateIpPart(allowedIpParts[3], remoteIpParts[3], allowedIp, remoteIp);
    }

    private static boolean validateIpPart(String allowed, String remote, String allowedIp, String remoteIp)
            throws IpWhiteListException {
        if ("*".equals(allowed))
            return true;

        int dash = allowed.indexOf('-');
        try {
            if (dash == -1)
                return Integer.parseInt(allowed) == Integer.parseInt(remote);

            int from = Integer.parseInt(allowed.substring(0, dash));
            int to = Integer.parseInt(allowed.substring(dash + 1));
            int rem = Integer.parseInt(remote);

            return from <= rem && rem <= to;
        }
        catch (NumberFormatException e) {
            throw new IpWhiteListException("Integer parsing error. allowed=" + allowedIp + ", remote=" + remoteIp);
        }
    }

    public static String checkIpMask(String ip) {
        String[] ipParts = ip.split("\\.");
        if (ipParts.length != 4)
            return "IP address must have 4 parts";

        String message = checkIpMaskPart(ipParts[0]);
        if (message != null)
            return message;
        message = checkIpMaskPart(ipParts[1]);
        if (message != null)
            return message;
        message = checkIpMaskPart(ipParts[2]);
        if (message != null)
            return message;
        message = checkIpMaskPart(ipParts[3]);
        if (message != null)
            return message;

        return null;
    }

    private static String checkIpMaskPart(String part) {
        if ("*".equals(part))
            return null;

        int dash = part.indexOf('-');
        try {
            if (dash == -1) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255)
                    return "Value out of range in '" + part + "'";
            }
            else {
                int from = Integer.parseInt(part.substring(0, dash));
                if (from < 0 || from > 255)
                    return "'From' value out of range in '" + part + "'";

                int to = Integer.parseInt(part.substring(dash + 1));
                if (to < 0 || to > 255)
                    return "'To' value out of range in '" + part + "'";

                if (from > to)
                    return "'From' value is greater than 'To' value in '" + part + "'";
            }
        }
        catch (NumberFormatException e) {
            return "Integer parsing error in '" + part + "'";
        }

        return null;
    }

    public static byte[] toIpAddress(String addr) throws IllegalArgumentException {
        if (addr == null)
            throw new IllegalArgumentException("Invalid address: (null)");

        String[] parts = addr.split("\\.");
        if (parts.length != 4)
            throw new IllegalArgumentException("IP address must have 4 parts");

        byte[] ip = new byte[4];
        for (int i = 0; i < 4; i++) {
            try {
                int part = Integer.parseInt(parts[i]);
                if (part < 0 || part > 255)
                    throw new IllegalArgumentException("Value out of range in '" + parts[i] + "'");
                ip[i] = (byte) part;
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Integer parsing error in '" + parts[i] + "'");
            }
        }

        return ip;
    }

    public static String toIpString(byte[] b) throws IllegalArgumentException {
        if (b.length != 4)
            throw new IllegalArgumentException("IP address must have 4 parts");

        StringBuilder sb = new StringBuilder();
        sb.append(b[0] & 0xff);
        for (int i = 1; i < b.length; i++)
            sb.append('.').append(b[i] & 0xff);
        return sb.toString();
    }
}
