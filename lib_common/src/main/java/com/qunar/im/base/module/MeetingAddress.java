package com.qunar.im.base.module;

import java.util.List;

public class MeetingAddress {
    private long version;//地址版本号

    private List<Address> addresses;//地址列表

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public static class Address{
        private String AddressName;//地址名称
        private long AddressNumber;//地址编号
        List<MeetingRoom> meetingRooms;//地址所拥有会议室

        public String getAddressName() {
            return AddressName;
        }

        public void setAddressName(String addressName) {
            AddressName = addressName;
        }

        public long getAddressNumber() {
            return AddressNumber;
        }

        public void setAddressNumber(long addressNumber) {
            AddressNumber = addressNumber;
        }

        public List<MeetingRoom> getMeetingRooms() {
            return meetingRooms;
        }

        public void setMeetingRooms(List<MeetingRoom> meetingRooms) {
            this.meetingRooms = meetingRooms;
        }
    }

    public static class MeetingRoom{
        private String RoomName;//会议室名称
        private long RoomNumber;//会议室编号
        private long AddressNumber;//会议室所属地址编号
        private String RoomDetails;//描述会议室所带能力大小等

        public String getRoomDetails() {
            return RoomDetails;
        }

        public void setRoomDetails(String roomDetails) {
            RoomDetails = roomDetails;
        }

        public String getRoomName() {
            return RoomName;
        }

        public void setRoomName(String roomName) {
            RoomName = roomName;
        }

        public long getRoomNumber() {
            return RoomNumber;
        }

        public void setRoomNumber(long roomNumber) {
            RoomNumber = roomNumber;
        }

        public long getAddressNumber() {
            return AddressNumber;
        }

        public void setAddressNumber(long addressNumber) {
            AddressNumber = addressNumber;
        }
    }
}
