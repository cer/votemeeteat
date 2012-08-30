require 'sinatra'

post '/' do
  phone_number = params[:From]
  registration_url = "#{ENV['REGISTRATION_URL']}?phoneNumber=#{URI.encode(phone_number, "+")}"
  <<-eof
	<Response>
		<Sms>To complete registration please go to #{registration_url}
	    </Sms>
	</Response>  
eof
end
