clc
clear
load net;

%***************************
%��ȡ�������
% load data.mat;
%***************************
TEMP = load( 'data.mat');
tmp = struct2cell(TEMP);
tmp=char(tmp);
row=size(tmp,1);
string=[];
target=[];
for i=1:1:row
string=[string;tmp(i,:)];
end
%string='022734510227345102263661032639910325320003243101022431990223309903222F9903222E99';
%string='91111111911111119111111191111111911111119111111191111111911111119111111191111111';
col=size(string,2);
ch1=string(1:8:col);
ch2=string(2:8:col);
ch3=string(3:8:col);
ch4=string(4:8:col);
ch5=string(5:8:col);
ch6=string(6:8:col);
ch7=string(7:8:col);
ch8=string(8:8:col);
str1=[ch1',ch2'];
str2=[ch3',ch4'];
str3=[ch5',ch6'];
str4=[ch7',ch8'];
col1=hex2dec(str1);
col2=hex2dec(str2);
col3=hex2dec(str3);
col4=hex2dec(str4);
initdata=[col1 col2 col3 col4];



[row,col]=size(initdata);
% for i=1:1:col-1
%     for j=1:1:row
%         if j>1&&initdata(j,i)>=5*initdata(j-1,i)
%             initdata(j,i)=initdata(j-1,i);
%         end
%     end
% end
yan=0;
for i=1:1:row
    if initdata(i,4)>=100
        yan=1;
    end
end
if yan==1 && mean(initdata(:,2))>=10
    kind='0x01';
else
    if (mean(initdata(:,3))<3 && mean(initdata(:,1)<=50 && mean(initdata(:,2))<10)) || (mean(initdata(:,2))<=10 && mean(initdata(:,1)<10))
        target=1;
    else
        target=0;
    end
%     xs
   if mean(initdata(:,1))>2.5*mean(initdata(:,2))
   for i=1:1:row
    initdata(i,3)=10;
    initdata(i,2)=10;
   end
   end
%       if mean(initdata(:,1))>(mean(initdata(:,2))+30)
%    for i=1:1:row
%     initdata(i,3)=10;
%    end
%    end
%    xj
if mean(initdata(:,1))>160
       for i=1:1:row
    initdata(i,2)=10;
    initdata(i,3)=10;
        end
end
% if (mean(initdata(:,2))>3*mean(initdata(:,1))) && mean(initdata(:,2))>3*mean(initdata(:,3))
%        for i=1:1:row
%     initdata(i,1)=16;
%      initdata(i,2)=initdata(i,2)+10;
%         end
% end
   
%    if (mean(initdata(:,2))+mean(initdata(:,3)))>2*mean(initdata(:,1))
%     for i=1:1:row
%         initdata(i,1)=10;
%     end
%    end
    % Ԥ����
    
    

    
    meantestdata1=mean(initdata(1:10,1:3));
    meantestdata2=mean(initdata(11:20,1:3));
    meantestdata3=mean(initdata(21:30,1:3));
    vartestdata1=var(initdata(1:10,1:3));
    vartestdata2=var(initdata(11:20,1:3));
    vartestdata3=var(initdata(21:30,1:3));
    mediantestdata1=median(initdata(1:10,1:3));
    mediantestdata2=median(initdata(11:20,1:3));
    mediantestdata3=median(initdata(21:30,1:3));
%     mintestdata=min(initdata(:,1:3));
%     maxtestdata=max(initdata(:,1:3));
%     max_mintestdata=max(initdata(:,1:3))-min(initdata(:,1:3));
    feature_first1=mean(initdata(1:10,1)-initdata(1:10,2));
    feature_first2=mean(initdata(11:20,1)-initdata(11:20,2));
    feature_first3=mean(initdata(21:30,1)-initdata(21:30,2));
    
    feature_second1=mean(initdata(1:10,1)-initdata(1:10,3));
    feature_second2=mean(initdata(11:20,1)-initdata(11:20,3));
    feature_second3=mean(initdata(21:30,1)-initdata(21:30,3));
%     feature_thrid=mean(initdata(:,1)+initdata(:,2)+initdata(:,3));
    if feature_first1>feature_second1
        feature_fourth1=1;
    else
        feature_fourth1=0;
    end
        if feature_first2>feature_second2
        feature_fourth2=1;
    else
        feature_fourth2=0;
        end
        if feature_first3>feature_second3
        feature_fourth3=1;
    else
        feature_fourth3=0;
    end
%     output_data=mapminmax('apply',[meantestdata,vartestdata,mediantestdata]',train_ps4);
%     input_data=output_data;
    input_data1=[meantestdata1,feature_first1,feature_second1,feature_fourth1]';
    input_data2=[meantestdata2,feature_first2,feature_second2,feature_fourth2]';
    input_data3=[meantestdata3,feature_first3,feature_second3,feature_fourth3]';


% ����
    y11=sim(net14,input_data);
    y21=sim(net24,input_data);
    y31=sim(net34,input_data);
    y41=sim(net44,input_data);
    y51=sim(net54,input_data);
    
    y12=sim(net14,input_data);
    y22=sim(net24,input_data);
    y32=sim(net34,input_data);
    y42=sim(net44,input_data);
    y52=sim(net54,input_data);
    
    y13=sim(net14,input_data);
    y23=sim(net24,input_data);
    y33=sim(net34,input_data);
    y43=sim(net44,input_data);
    y53=sim(net54,input_data);
    
    [~,kind11]=max(y11);
    [~,kind21]=max(y21);
    [~,kind31]=max(y31);
    [~,kind41]=max(y41);
    [~,kind51]=max(y51);

    [~,kind12]=max(y12);
    [~,kind22]=max(y22);
    [~,kind32]=max(y32);
    [~,kind42]=max(y42);
    [~,kind52]=max(y52);
    
    [~,kind13]=max(y13);
    [~,kind23]=max(y23);
    [~,kind33]=max(y33);
    [~,kind43]=max(y43);
    [~,kind53]=max(y53);
% ͶƱ
    xs=0;
    jp=0;
    yq=0;
    kind_select1=[kind11;kind21;kind31;kind41;kind51];
    kind_select2=[kind12;kind22;kind32;kind42;kind52];
    kind_select3=[kind13;kind23;kind33;kind43;kind53];
    for j=1:1:size(kind_select1,1)
        if kind_select1(j)==3
            xs=xs+1;
        elseif kind_select1(j)==2
            jp=jp+1;
        else
            yq=yq+1;
        end
    end
        for j=1:1:size(kind_select2,1)
        if kind_select2(j)==3
            xs=xs+1;
        elseif kind_select2(j)==2
            jp=jp+1;
        else
            yq=yq+1;
        end
        end
        for j=1:1:size(kind_select3,1)
        if kind_select3(j)==3
            xs=xs+1;
        elseif kind_select3(j)==2
            jp=jp+1;
        else
            yq=yq+1;
        end
    end
        [~,kind_num]=max([xs,jp,yq]);
%���ת��
if target==1
   kind='0x00';
else
if kind_num==1
    kind='0x02';
elseif kind_num==2
    kind='0x03';
elseif kind_num==3
    kind='0x04';
else
    king='0x00';
end
end
end
% Ũ�ȷּ�
data=mean(initdata);

if strcmp(kind,'0x00')
    if data(1,4)>=200
        level='0x05';
    elseif data(1,4)>=150
        level='0x04';
    elseif data(1,4)>=100
        level='0x03';
    elseif data(1,4)>=50
        level='0x02';
    else
        level='0x01';
    end
elseif strcmp(kind,'0x01')
    if data(1,4)>=270
        level='0x05';
    elseif data(1,4)>=230
        level='0x04';
    elseif data(1,4)>=190
        level='0x03';
    elseif data(1,4)>=150
        level='0x02';
    else
        level='0x01';
    end

elseif strcmp(kind,'0x02')
    if data(1,1)>=150
        level='0x05';
    elseif data(1,1)>=100
        level='0x04';
    elseif data(1,1)>=50
        level='0x03';
    elseif data(1,1)>=20
        level='0x02';
    else
        level='0x01';
    end
elseif strcmp(kind,'0x03')
    if data(1,2)>=40
        level='0x05';
    elseif data(1,2)>=30
        level='0x04';
    elseif data(1,2)>=20
        level='0x03';
    elseif data(1,2)>=10
        level='0x02';
    else
        level='0x01';
    end
elseif strcmp(kind,'0x04')
    if data(1,2)>=60
        level='0x05';
    elseif data(1,2)>=45
        level='0x04';
    elseif data(1,2)>=30
        level='0x03';
    elseif data(1,2)>=15
        level='0x02';
    else
        level='0x01';
    end

else
    level='0x00';        
end
kind
level