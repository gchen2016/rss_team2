/* Auto-generated by genmsg_cpp for file /home/rss-student/RSS-I-group/rss_msgs/msg/BreakBeamMsg.msg */
#ifndef RSS_MSGS_MESSAGE_BREAKBEAMMSG_H
#define RSS_MSGS_MESSAGE_BREAKBEAMMSG_H
#include <string>
#include <vector>
#include <map>
#include <ostream>
#include "ros/serialization.h"
#include "ros/builtin_message_traits.h"
#include "ros/message_operations.h"
#include "ros/time.h"

#include "ros/macros.h"

#include "ros/assert.h"


namespace rss_msgs
{
template <class ContainerAllocator>
struct BreakBeamMsg_ {
  typedef BreakBeamMsg_<ContainerAllocator> Type;

  BreakBeamMsg_()
  : beamBroken(false)
  {
  }

  BreakBeamMsg_(const ContainerAllocator& _alloc)
  : beamBroken(false)
  {
  }

  typedef uint8_t _beamBroken_type;
  uint8_t beamBroken;


private:
  static const char* __s_getDataType_() { return "rss_msgs/BreakBeamMsg"; }
public:
  ROS_DEPRECATED static const std::string __s_getDataType() { return __s_getDataType_(); }

  ROS_DEPRECATED const std::string __getDataType() const { return __s_getDataType_(); }

private:
  static const char* __s_getMD5Sum_() { return "5c99d1d4bed9929256313b1c7b10c3bd"; }
public:
  ROS_DEPRECATED static const std::string __s_getMD5Sum() { return __s_getMD5Sum_(); }

  ROS_DEPRECATED const std::string __getMD5Sum() const { return __s_getMD5Sum_(); }

private:
  static const char* __s_getMessageDefinition_() { return "bool beamBroken\n\
"; }
public:
  ROS_DEPRECATED static const std::string __s_getMessageDefinition() { return __s_getMessageDefinition_(); }

  ROS_DEPRECATED const std::string __getMessageDefinition() const { return __s_getMessageDefinition_(); }

  ROS_DEPRECATED virtual uint8_t *serialize(uint8_t *write_ptr, uint32_t seq) const
  {
    ros::serialization::OStream stream(write_ptr, 1000000000);
    ros::serialization::serialize(stream, beamBroken);
    return stream.getData();
  }

  ROS_DEPRECATED virtual uint8_t *deserialize(uint8_t *read_ptr)
  {
    ros::serialization::IStream stream(read_ptr, 1000000000);
    ros::serialization::deserialize(stream, beamBroken);
    return stream.getData();
  }

  ROS_DEPRECATED virtual uint32_t serializationLength() const
  {
    uint32_t size = 0;
    size += ros::serialization::serializationLength(beamBroken);
    return size;
  }

  typedef boost::shared_ptr< ::rss_msgs::BreakBeamMsg_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::rss_msgs::BreakBeamMsg_<ContainerAllocator>  const> ConstPtr;
  boost::shared_ptr<std::map<std::string, std::string> > __connection_header;
}; // struct BreakBeamMsg
typedef  ::rss_msgs::BreakBeamMsg_<std::allocator<void> > BreakBeamMsg;

typedef boost::shared_ptr< ::rss_msgs::BreakBeamMsg> BreakBeamMsgPtr;
typedef boost::shared_ptr< ::rss_msgs::BreakBeamMsg const> BreakBeamMsgConstPtr;


template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const  ::rss_msgs::BreakBeamMsg_<ContainerAllocator> & v)
{
  ros::message_operations::Printer< ::rss_msgs::BreakBeamMsg_<ContainerAllocator> >::stream(s, "", v);
  return s;}

} // namespace rss_msgs

namespace ros
{
namespace message_traits
{
template<class ContainerAllocator> struct IsMessage< ::rss_msgs::BreakBeamMsg_<ContainerAllocator> > : public TrueType {};
template<class ContainerAllocator> struct IsMessage< ::rss_msgs::BreakBeamMsg_<ContainerAllocator>  const> : public TrueType {};
template<class ContainerAllocator>
struct MD5Sum< ::rss_msgs::BreakBeamMsg_<ContainerAllocator> > {
  static const char* value() 
  {
    return "5c99d1d4bed9929256313b1c7b10c3bd";
  }

  static const char* value(const  ::rss_msgs::BreakBeamMsg_<ContainerAllocator> &) { return value(); } 
  static const uint64_t static_value1 = 0x5c99d1d4bed99292ULL;
  static const uint64_t static_value2 = 0x56313b1c7b10c3bdULL;
};

template<class ContainerAllocator>
struct DataType< ::rss_msgs::BreakBeamMsg_<ContainerAllocator> > {
  static const char* value() 
  {
    return "rss_msgs/BreakBeamMsg";
  }

  static const char* value(const  ::rss_msgs::BreakBeamMsg_<ContainerAllocator> &) { return value(); } 
};

template<class ContainerAllocator>
struct Definition< ::rss_msgs::BreakBeamMsg_<ContainerAllocator> > {
  static const char* value() 
  {
    return "bool beamBroken\n\
";
  }

  static const char* value(const  ::rss_msgs::BreakBeamMsg_<ContainerAllocator> &) { return value(); } 
};

template<class ContainerAllocator> struct IsFixedSize< ::rss_msgs::BreakBeamMsg_<ContainerAllocator> > : public TrueType {};
} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

template<class ContainerAllocator> struct Serializer< ::rss_msgs::BreakBeamMsg_<ContainerAllocator> >
{
  template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
  {
    stream.next(m.beamBroken);
  }

  ROS_DECLARE_ALLINONE_SERIALIZER;
}; // struct BreakBeamMsg_
} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::rss_msgs::BreakBeamMsg_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const  ::rss_msgs::BreakBeamMsg_<ContainerAllocator> & v) 
  {
    s << indent << "beamBroken: ";
    Printer<uint8_t>::stream(s, indent + "  ", v.beamBroken);
  }
};


} // namespace message_operations
} // namespace ros

#endif // RSS_MSGS_MESSAGE_BREAKBEAMMSG_H

